package com.example.raon.features.chat.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.common.AppConstants
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.dto.ChatMessageDto
// ChatRoomDetailDto import 확인
import com.example.raon.features.chat.data.remote.dto.ChatRoomDetailDataDto
import com.example.raon.features.chat.data.remote.dto.MessageDto
import com.example.raon.features.chat.data.remote.dto.UserInChatDetailDto // [수정] 이 import 문을 추가합니다.
// [추가] 새로 만든 이미지 분석 DTO import
//import com.example.raon.features.chat.data.remote.dto.ai.ImageAnalysisData
import com.example.raon.features.chat.data.remote.dto.ai.FraudDetectionRequestDto
import com.example.raon.features.chat.data.remote.dto.ai.MessageInFraudRequestDto
import com.example.raon.features.chat.domain.model.ChatMessage // ChatMessage import 확인
import com.example.raon.features.chat.domain.repository.ChatRepository
// ItemRepository는 이제 필요 없음
// import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.user.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async // async import 확인
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch // Flow 에러 처리를 위한 import
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime // 시간 정렬/포맷팅 위해 추가
import java.time.format.DateTimeFormatter // 시간 정렬/포맷팅 위해 추가
import java.time.temporal.ChronoUnit // 시간 차이 계산 위해 추가
import javax.inject.Inject

// 👆 [주석 1] 완료 👆


// 화면 상단 바 상품 정보 data class
data class ChatProductInfo(
    val productName: String,
    val price: Int?,
    val status: String,
    val viewableThumbnailUrl: String?
)

// 채팅 화면 전체 UI 상태
data class ChatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = emptyList(), // 오래된 메시지 -> 최신 메시지 순서로 저장됨
    val fraudWarningMessage: String? = null,
    val productInfo: ChatProductInfo? = null,
    val opponentNickname: String? = null,
    val isCurrentUserBuyer: Boolean = false // [추가] 현재 유저가 구매자인지 여부
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val imageStorageRepository: ImageStorageRepository, // ImageStorageRepository 주입 확인
    private val stompService: StompService,
    savedStateHandle: SavedStateHandle
    // ItemRepository 주입 제거
) : ViewModel() {

    // chatRoomId만 Navigation으로 받음
    val chatRoomId: Long = savedStateHandle.get<String>("chatRoomId")?.toLongOrNull() ?: -1L

    private val gson = Gson()
    private val _myUserId = MutableStateFlow<Int?>(null)
    val myUserId = _myUserId.asStateFlow()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("ChatViewModel", "0. Initializing with chatId: $chatRoomId")
        viewModelScope.launch {
            _myUserId.value = userRepository.getUserProfile().first()?.userId
            Log.d("ChatViewModel", "My User ID loaded: ${_myUserId.value}")

            if (chatRoomId != -1L && _myUserId.value != null) {
                loadInitialData() // 데이터 로딩 시작
                connectAndObserveStomp() // STOMP 연결 별도 시작
            } else {
                val errorMsg =
                    if (chatRoomId == -1L) "유효하지 않은 채팅방 ID입니다." else "사용자 정보를 불러올 수 없습니다."
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                Log.e("ChatViewModel", "Initialization failed: $errorMsg")
            }
        }
    }

    // 초기 데이터 로딩 함수 (ItemRepository 호출 제거 버전)
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // 로딩 시작
            Log.d("ChatViewModel", "1. Starting initial data load...")

            var chatDetailsData: ChatRoomDetailDataDto? = null // API 응답 저장 변수
            var actualOpponentNickname: String? = null
            var isBuyer: Boolean = false // [추가] 구매자인지 판별할 임시 변수

            try {
                // --- 1. 채팅방 상세 정보 가져오기 (API 호출) ---
                Log.d("ChatViewModel", "1a. Fetching chat room details for ID: $chatRoomId")
                chatDetailsData =
                    when (val detailsResult = chatRepository.getChatRoomDetails(chatRoomId)) {
                        is ApiResult.Success -> detailsResult.data.data
                            ?: throw Exception("Chat details data is null")

                        is ApiResult.Error -> throw Exception("Chat details load failed: ${detailsResult.code}")
                        is ApiResult.Exception -> throw detailsResult.e // 원본 예외 다시 던지기
                    }

                // [수정] 상대방 정보 찾기 및 구매자 여부 판별
                // [수정] 타입을 ChatRoomDetailDataDto.UserBrief -> UserInChatDetailDto 로 변경
                val opponentUser: UserInChatDetailDto
                if (_myUserId.value == chatDetailsData.seller.userId) {
                    opponentUser = chatDetailsData.buyer
                    isBuyer = false // 나는 판매자
                } else {
                    opponentUser = chatDetailsData.seller
                    isBuyer = true // 나는 구매자
                }
                actualOpponentNickname = opponentUser.nickname

                Log.d(
                    "ChatViewModel",
                    "1b. Chat details loaded: ItemId=${chatDetailsData.product.productId}, Opponent='${actualOpponentNickname}' (ID:${opponentUser.userId}), IsBuyer=$isBuyer"
                )

                // --- 2. 상품 썸네일 Presigned URL 가져오기 (비동기) ---
                val productInfoDeferred = async {
                    Log.d(
                        "ChatViewModel",
                        "2a. Requesting Presigned URL for product: ${chatDetailsData.product.productId}"
                    )
                    var thumbnailUrl: String? = null
                    val thumbnailKey = chatDetailsData.product.thumbnail
                        ?.removePrefix(AppConstants.S3_BASE_URL)

                    if (thumbnailKey != null) {
                        try {
                            thumbnailUrl = imageStorageRepository.getPresignedImageUrl(thumbnailKey)
                                .getOrNull()
                            Log.d("ChatViewModel", "2b. Presigned URL received: $thumbnailUrl")
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "❌ Failed to load Thumbnail Presigned URL", e)
                        }
                    } else {
                        Log.w("ChatViewModel", "2b. No thumbnail key found in chat details.")
                    }

                    val productStatusText = when (chatDetailsData.product.status) {
                        "AVAILABLE" -> "판매중"
                        "RESERVED" -> "예약중"
                        "SOLD" -> "판매완료"
                        else -> chatDetailsData.product.status
                            ?: "상태 정보 없음" // 알 수 없거나 null이면 원래 값 사용
                    }

                    ChatProductInfo(
                        productName = chatDetailsData.product.title ?: "상품 이름 없음",
                        price = chatDetailsData.product.price,
                        status = productStatusText,
                        viewableThumbnailUrl = thumbnailUrl
                    )
                }

                // --- 3. 과거 메시지 로드 (비동기) ---
                val messagesDeferred = async {
                    Log.d("ChatViewModel", "3a. Loading initial messages...")
                    loadInitialMessages()
                }

                // --- 4. 상품 정보(Presigned URL 포함) 및 메시지 로딩 완료 대기 ---
                val loadedProductInfo = productInfoDeferred.await()
                messagesDeferred.await()
                Log.d("ChatViewModel", "Product info & Initial messages loading complete.")

                // --- 5. 최종 UI 상태 업데이트 (로딩 종료) ---
                _uiState.update {
                    // 기존 메시지 상태는 loadInitialMessages에서 업데이트 되므로 유지하면서 업데이트
                    it.copy(
                        isLoading = false,
                        productInfo = loadedProductInfo,
                        opponentNickname = actualOpponentNickname,
                        isCurrentUserBuyer = isBuyer // [추가] 구매자 여부 상태에 저장
                    )
                }
                Log.d("ChatViewModel", "Initial data load complete. UI State Updated.")

                // --- 6. 메시지 읽음 처리 (백그라운드) ---
                markMessagesAsRead()

            } catch (e: Exception) {
                // 전체 로딩 과정에서 에러 발생 시
                Log.e("ChatViewModel", "❌ Error during initial data load", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "채팅방 정보를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    // STOMP 연결 및 메시지 구독 전용 함수
    private fun connectAndObserveStomp() {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Starting STOMP connection and observation...")
            try {
                chatRepository.connectStomp(chatRoomId = chatRoomId)
                Log.d("ChatViewModel", "STOMP Connected. Observing messages...")
                chatRepository.observeMessages(chatRoomId)
                    .catch { e -> // Flow 에러 처리
                        Log.e("ChatViewModel", "❌ STOMP message observation error", e)
                        _uiState.update { it.copy(errorMessage = "실시간 메시지 수신 중 오류가 발생했습니다.") }
                    }
                    .collect { jsonString -> // 메시지 수신
                        try {
                            Log.d(
                                "ChatViewModel",
                                "Received STOMP message: 데이터 담기전"
                            )

                            val chatMessageDto =
                                gson.fromJson(jsonString, ChatMessageDto::class.java)
                            val chatMessage = chatMessageDto.toDomainModel(myUserId.value)
                            Log.d(
                                "ChatViewModel",
                                "Received STOMP message: ${chatMessage.messageId}"
                            )

                            // [수정] 1. 상대방이 보낸 메시지인지 확인
                            if (!chatMessage.isFromMe) {
                                // [수정] 2. 별도 코루틴으로 읽음 처리 API 호출
                                //      -> collect 블록의 실행을 방해하지 않음
                                //      -> API가 모든 메시지를 읽음 처리하므로 한 번만 호출해도 됨
                                viewModelScope.launch {
                                    markMessagesAsRead()
                                }
                            }

                            _uiState.update { currentState ->
                                // 메시지 리스트 뒤에 새 메시지 추가 (중복 방지 포함)
                                // [중요] 이 ID 중복 방지 로직이 Optimistic UI와 충돌할 수 있음
                                if (currentState.messages.none { it.messageId == chatMessage.messageId }) {
                                    // [수정] 1. 타임스탬프로 1차 정렬, 2. messageId로 2차 정렬
                                    currentState.copy(
                                        messages = (currentState.messages + listOf(chatMessage)) // 리스트 뒤에 추가
                                            .sortedWith(compareBy<ChatMessage> { msg ->
                                                parseTimestamp(
                                                    msg.originalTimestamp
                                                )
                                            }
                                                .thenBy { it.messageId })
                                    )
                                } else {
                                    Log.w(
                                        "ChatViewModel",
                                        "Duplicate STOMP message received: ${chatMessage.messageId}"
                                    )
                                    currentState // 중복이면 상태 변경 없음
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "❌ STOMP message parsing failed: $jsonString", e)
                        }
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "❌ STOMP connection failed", e)
                _uiState.update { it.copy(errorMessage = "실시간 채팅 서버에 연결할 수 없습니다.") }
            }
            Log.d("ChatViewModel", "STOMP observation finished or failed.")
        }
    }

    // 과거 메시지 로드 함수
    private suspend fun loadInitialMessages() {
        // isLoading 상태 업데이트는 loadInitialData에서 처리
        when (val result = chatRepository.getMessageList(chatRoomId, page = 0)) {
            is ApiResult.Success -> {
                val messageDtos = result.data.data?.messages ?: emptyList()
                _uiState.update {
                    it.copy(
                        // [수정] 1. 타임스탬프로 1차 정렬, 2. messageId로 2차 정렬
                        messages = messageDtos.map { dto -> dto.toDomainModel(myUserId.value) }
                            .sortedWith(compareBy<ChatMessage> { msg -> parseTimestamp(msg.originalTimestamp) }
                                .thenBy { it.messageId })
                    )
                }
                Log.d("ChatViewModel", "Initial messages loaded: ${messageDtos.size}")
            }

            is ApiResult.Error -> {
                Log.e("ChatViewModel", "❌ Error loading initial messages: ${result.code}")
                _uiState.update { it.copy(errorMessage = "메시지 로딩 실패") }
            }

            is ApiResult.Exception -> {
                Log.e("ChatViewModel", "❌ Exception loading initial messages", result.e)
                _uiState.update { it.copy(errorMessage = "메시지 로딩 중 오류 발생") }
            }
        }
    }

    // [수정] 메시지 전송 함수 (Optimistic UI 적용)
    fun sendMessage(text: String) {
        if (text.isBlank() || chatRoomId == -1L) return

        // [추가] 1. UI 즉시 업데이트를 위한 유저 ID 확인
        val currentMyId = _myUserId.value ?: run {
            Log.e("ChatViewModel", "Cannot send message: My User ID is null")
            return // ID 없으면 전송 불가
        }

        // [추가] 2. 정렬 및 표시에 사용할 시간 생성
        val now = LocalDateTime.now()
        // 파일 하단에 정의된 private formatter를 사용하여 "yyyy-MM-dd HH:mm:ss" 형식 생성
        val originalTimestamp = now.format(timestampFormatter)
        val displayTimestamp = "방금 전" // UI에는 "방금 전"으로 표시

        // [추가] 3. UI에 즉시 표시할 ChatMessage 객체 생성
        // (messageId는 임시로 고유한 값(현재 시간 ms)을 사용)
        // (서버가 STOMP로 이 메시지를 echo하지 않는다는 가정 하의 로직)
        val optimisticMessage = ChatMessage(
            messageId = System.currentTimeMillis(), // 임시 고유 ID
            chatRoomId = chatRoomId,
            senderId = currentMyId,
            senderNickname = "나", // Bubble에 표시되진 않지만 채워둠
            senderProfileUrl = null,
            content = text,
            imageUrl = null,
            timestamp = displayTimestamp, // 화면 표시용
            isFromMe = true,
            originalTimestamp = originalTimestamp // 정렬용
        )

        // [추가] 4. UI 상태에 즉시 반영 (리스트에 추가하고 시간순 정렬)
        _uiState.update { currentState ->
            currentState.copy(
                // [수정] 1. 타임스탬프로 1차 정렬, 2. messageId로 2차 정렬
                messages = (currentState.messages + optimisticMessage)
                    .sortedWith(compareBy<ChatMessage> { msg -> parseTimestamp(msg.originalTimestamp) }
                        .thenBy { it.messageId })
            )
        }

        // [기존 로직] 5. 실제 서버로 메시지 전송 (HTTP POST)
        viewModelScope.launch {
            when (val result = chatRepository.sendMessage(chatRoomId, text)) {
                is ApiResult.Success -> {
                    Log.d("ChatViewModel", "Message sent successfully (HTTP POST)")
                    // (성공 시)
                    // 만약 서버가 보낸 메시지의 '진짜' messageId를 응답으로 준다면,
                    // 여기서 optimisticMessage(임시 ID)를
                    // 서버가 준 진짜 ID로 교체하는 로직을 추가하면 더 견고해집니다.
                }

                is ApiResult.Error -> {
                    Log.e("ChatViewModel", "Message send API error: ${result.code}")
                    // (실패 시)
                    // UI에서 해당 메시지를 찾아 "전송 실패" 상태로 바꾸는 로직 추가 가능
                }

                is ApiResult.Exception -> {
                    Log.e("ChatViewModel", "Message send network exception", result.e)
                    // (실패 시)
                    // UI에서 해당 메시지를 찾아 "전송 실패" 상태로 바꾸는 로직 추가 가능
                }
            }
        }
    }


    // (텍스트) 사기 탐지 함수
    fun detectFraud() {
        viewModelScope.launch {
            val currentMyId = myUserId.value ?: run {
                Log.e("ChatViewModel", "Cannot detect fraud: My User ID is null")
                _uiState.update { it.copy(fraudWarningMessage = "사용자 정보 오류로 분석할 수 없습니다.") }
                return@launch
            }
            val currentMessages = _uiState.value.messages
            if (currentMessages.isEmpty()) {
                Log.w("ChatViewModel", "Cannot detect fraud: No messages")
                _uiState.update { it.copy(fraudWarningMessage = "분석할 메시지가 없습니다.") }
                return@launch
            }

            // ChatMessage -> MessageInFraudRequestDto 변환
            val messageDtos = currentMessages.mapNotNull { chatMessage ->
                MessageInFraudRequestDto(
                    messageId = chatMessage.messageId,
                    senderId = chatMessage.senderId,
                    content = chatMessage.content,
                    sentAt = chatMessage.originalTimestamp // 포맷팅 안된 원본 시간 전달
                )
            }
            val requestDto = FraudDetectionRequestDto(
                requesterId = currentMyId, // API가 Long을 요구하면 .toLong() 추가
                messages = messageDtos // 시간 오름차순 상태
            )

            Log.d("ChatViewModel", "Requesting fraud detection...")
            when (val result = chatRepository.detectFraud(chatRoomId, requestDto)) {
                is ApiResult.Success -> {
                    val analysisResult = result.data.data
                    Log.d("ChatViewModel", "Fraud detection result: ${analysisResult?.result}")
                    _uiState.update { it.copy(fraudWarningMessage = analysisResult?.message) }
                }

                is ApiResult.Error -> {
                    Log.e("ChatViewModel", "Fraud detection API error: ${result.code}")
                    _uiState.update { it.copy(fraudWarningMessage = "분석 중 오류 발생 (API)") }
                }

                is ApiResult.Exception -> {
                    Log.e("ChatViewModel", "Fraud detection network exception", result.e)
                    _uiState.update { it.copy(fraudWarningMessage = "분석 중 오류 발생 (네트워크)") }
                }
            }
        }
    }

    // [수정] AI 이미지 분석 함수 (DANGER 처리 및 maxOfOrNull 오류 수정)
    fun analyzeImage() {
        viewModelScope.launch {
            Log.d("ChatViewModel", "AI Image Analysis Requested for chat: $chatRoomId")
            // 로딩 상태를 UI에 표시
            _uiState.update { it.copy(isLoading = true) }

            when (val result = chatRepository.analyzeImages(chatRoomId)) {
                is ApiResult.Success -> {
                    val analysisData = result.data.data
                    val results = analysisData?.results

                    if (results.isNullOrEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                fraudWarningMessage = "분석할 이미지가 없거나 분석에 실패했습니다."
                            )
                        }
                        Log.w("ChatViewModel", "Image analysis result is null or empty.")
                        return@launch
                    }

                    // [수정] "DANGER" 상태인 결과만 필터링
                    val dangerResults = results.filter { it.result == "DANGER" }

                    val message = if (dangerResults.isNotEmpty()) {
                        // "DANGER"가 하나 이상 있을 경우
                        val count = dangerResults.size

                        // [오류 수정] maxOfOrNull의 반환값(Double?)을 처리
                        // (it.maxScore ?: 0.0) -> 리스트 요소 중 maxScore가 null이면 0.0으로
                        // (?: 0.0) -> 리스트가 비어 maxOfOrNull이 null을 반환하면 0.0으로
                        val maxScorePercent = ((dangerResults.maxOfOrNull { it.maxScore ?: 0.0 }
                            ?: 0.0) * 100).toInt()

                        "🚨 AI 사진 분석 결과: $count 개의 이미지에서 심각한 사기 위험(최대 위험도: $maxScorePercent%)이 감지되었습니다."
                    } else {
                        // "DANGER"는 없지만 "WARNING"이 있는지 확인 (선택적)
                        val warningResults = results.filter { it.result == "WARNING" }
                        if (warningResults.isNotEmpty()) {
                            val count = warningResults.size

                            // [오류 수정] maxOfOrNull의 반환값(Double?)을 처리
                            val maxScorePercent =
                                ((warningResults.maxOfOrNull { it.maxScore ?: 0.0 }
                                    ?: 0.0) * 100).toInt()

                            "⚠️ AI 사진 분석 결과: $count 개의 이미지에서 사기 의심 정황(최대 위험도: $maxScorePercent%)이 발견되었습니다."
                        } else {
                            // 모든 이미지가 "SAFE"일 경우
                            "✅ AI 사진 분석 결과: 모든 이미지가 안전한 것으로 보입니다."
                        }
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fraudWarningMessage = message // fraudWarningMessage에 결과 표시
                        )
                    }
                    Log.d("ChatViewModel", "Image analysis success: $message")
                }

                is ApiResult.Error -> {
                    Log.e("ChatViewModel", "Image analysis API error: ${result.code}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fraudWarningMessage = "사진 분석 중 오류 발생 (API: ${result.code})"
                        )
                    }
                }

                is ApiResult.Exception -> {
                    Log.e("ChatViewModel", "Image analysis network exception", result.e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fraudWarningMessage = "사진 분석 중 오류 발생 (네트워크)"
                        )
                    }
                }
            }
        }
    }


    // 경고 배너 닫기 함수
    fun closeWarningBanner() {
        _uiState.update { it.copy(fraudWarningMessage = null) }
    }

    // 메시지 읽음 처리 함수
    private fun markMessagesAsRead() {
        if (chatRoomId == -1L) return

        // --- 기존 markMessagesAsRead() 함수 (수정 필요 없음) ---
        viewModelScope.launch { // 별도 코루틴
            Log.d("ChatViewModel", "Marking messages as read...")
            when (val result = chatRepository.markMessagesAsRead(chatRoomId)) {
                is ApiResult.Success -> Log.d("ChatViewModel", "✅ Messages marked as read.")
                is ApiResult.Error -> Log.e(
                    "ChatViewModel",
                    "❌ Failed to mark as read: ${result.code}"
                )

                is ApiResult.Exception -> Log.e(
                    "ChatViewModel",
                    "❌ Exception on marking as read",
                    result.e
                )
            }
        }
    }

    // ViewModel 소멸 시 STOMP 연결 해제
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            Log.d("ChatViewModel", "onCleared: Disconnecting STOMP...")
            chatRepository.disconnectStomp()
        }
    }
}

// DTO -> Domain 모델 변환 함수 (originalTimestamp 추가됨)
private fun MessageDto.toDomainModel(myUserId: Int?): ChatMessage {
    val formattedTimestamp = formatTimestampForDisplay(this.sentAt) // 화면 표시용 시간
    return ChatMessage(
        messageId = this.messageId,
        chatRoomId = this.chatId,
        senderId = this.sender.userId,
        senderNickname = this.sender.nickname,
        senderProfileUrl = this.sender.profileImage,
        content = this.content ?: "",
        imageUrl = this.imageUrl,
        timestamp = formattedTimestamp, // 화면 표시용 시간
        isFromMe = this.sender.userId == myUserId,
        originalTimestamp = this.sentAt // 정렬 및 API 전송용 원본 시간 추가
    )
}

// DTO -> Domain 모델 변환 함수 (STOMP 메시지용) (originalTimestamp 추가됨)
private fun ChatMessageDto.toDomainModel(myUserId: Int?): ChatMessage {
    val formattedTimestamp = formatTimestampForDisplay(this.sentAt) // 화면 표시용 시간
    return ChatMessage(
        messageId = this.messageId,
        chatRoomId = this.chatId,
        senderId = this.sender.userId,
        senderNickname = this.sender.nickname,
        senderProfileUrl = this.sender.profileImage,
        content = this.content ?: "",
        imageUrl = this.imageUrl,
        timestamp = formattedTimestamp, // 화면 표시용 시간
        isFromMe = this.sender.userId == myUserId,
        originalTimestamp = this.sentAt // 정렬 및 API 전송용 원본 시간 추가
    )
}

// 시간 문자열 -> LocalDateTime 변환 함수 (정렬용)
// API 응답 형식 "yyyy-MM-dd HH:mm:ss" 또는 ISO 형식 처리
private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private fun parseTimestamp(dateTimeString: String): LocalDateTime? {
    // [수정] null 또는 빈 문자열일 경우 즉시 null 반환
    if (dateTimeString.isNullOrBlank()) {
        Log.w("TimestampParse", "Timestamp string is null or blank.")
        return null
    }

    return try {
        // Z 또는 T 가 포함된 ISO 형식이면 OffsetDateTime으로 먼저 파싱 후 LocalDateTime으로 변환
        if (dateTimeString.contains("T") || dateTimeString.endsWith("Z")) {
            java.time.OffsetDateTime.parse(
                dateTimeString.replace(
                    " ",
                    "T"
                ) + if (!dateTimeString.endsWith("Z")) "Z" else ""
            ).toLocalDateTime() // 공백 T로 바꾸고 Z 추가
        }
        // "yyyy-MM-dd HH:mm:ss" 형식이면 바로 파싱
        else if (dateTimeString.contains(" ")) {
            LocalDateTime.parse(dateTimeString, timestampFormatter)
        } else {
            Log.w("TimestampParse", "Unexpected timestamp format: $dateTimeString")
            null
        }
    } catch (e: Exception) {
        Log.e("TimestampParse", "Failed to parse timestamp: $dateTimeString", e)
        null // 파싱 실패 시 null 반환 (리스트 맨 뒤로)
    }
}

// 시간 문자열 포맷 변환 함수 (상대 시간 표시용)
private fun formatTimestampForDisplay(dateTimeString: String): String {
    val parsedTime = parseTimestamp(dateTimeString) ?: return dateTimeString // 파싱 실패 시 원본 반환
    val now = LocalDateTime.now() // 현재 시간

    // 현재 시간과의 차이 계산
    val years = ChronoUnit.YEARS.between(parsedTime, now)
    val months = ChronoUnit.MONTHS.between(parsedTime, now)
    val days = ChronoUnit.DAYS.between(parsedTime, now)
    val hours = ChronoUnit.HOURS.between(parsedTime, now)
    val minutes = ChronoUnit.MINUTES.between(parsedTime, now)

    // 가장 큰 단위 기준으로 상대 시간 문자열 반환
    return when {
        years > 0 -> "${years}년 전"
        months > 0 -> "${months}달 전"
        days > 0 -> when (days) {
            1L -> "어제" // 1일 전은 "어제"로 표시
            else -> "${days}일 전"
        }

        hours > 0 -> "${hours}시간 전"
        minutes > 0 -> "${minutes}분 전"
        else -> "방금 전" // 1분 미만은 "방금 전"
    }
}
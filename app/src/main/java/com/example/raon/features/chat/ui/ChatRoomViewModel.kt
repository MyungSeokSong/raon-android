package com.example.raon.features.chat.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.dto.ChatMessageDto
import com.example.raon.features.chat.data.remote.dto.MessageDto
import com.example.raon.features.chat.domain.model.ChatMessage
import com.example.raon.features.chat.domain.repository.ChatRepository
import com.example.raon.features.user.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// ▼▼▼ 1. UI 상태에 로딩과 에러 상태를 추가합니다. ▼▼▼
data class ChatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = emptyList()
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository, // 👈 DataStore 대신 UserRepository를 주입
    private val stompService: StompService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // navigation으로 넘겨준 chatRoomId
    private val chatRoomId: Long = savedStateHandle.get<String>("chatRoomId")?.toLongOrNull() ?: -1L

    // 파싱을 위한 Gson
    private val gson = Gson()

    private val _myUserId = MutableStateFlow<Int?>(null)
    val myUserId = _myUserId.asStateFlow() // 외부에 공개할 때는 StateFlow로


    // ViewModel이 UI 상태를 직접 관리하도록 MutableStateFlow로 변경합니다.
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()


    // ▼▼▼ 1. 모든 초기화 로직을 하나의 init 블록으로 통합합니다. ▼▼▼
    init {

        Log.d("채팅프로세스", "채팅방 ID : $chatRoomId")
        Log.d("ChatRoomViewModel", "ChatRoomViewModel User ID : $myUserId")

        if (chatRoomId != -1L) {    // chatRoomId 잘 받았을 때
            loadInitialDataAndConnect() // 초기 데이터 가져오기, Stomp 소켓 연결
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "유효하지 않은 채팅방입니다.") }
        }

        // ViewModel이 생성될 때 UserRepository에서 사용자 프로필을 가져옴
        viewModelScope.launch {
            userRepository.getUserProfile()
                .collect { user -> // Flow<User?>를 수집
                    // user 객체가 null이 아닐 경우 userId를 상태에 업데이트
                    _myUserId.value = user?.userId
                }
        }
    }


    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    private fun loadInitialDataAndConnect() {   // 데이터 로드와, stomp 연결 함수
        viewModelScope.launch {
            try {
                // 소켓 연결
                chatRepository.connectStomp(chatRoomId = chatRoomId)

                // 과거 메시지 가져오기
                loadInitialMessages()

                chatRepository.observeMessages(chatRoomId).collect { jsonString ->

                    Log.d("StompService", "받은 채팅 데이터 : ${jsonString}")


                    try {
                        // 1. 수신된 JSON 문자열을 StompMessageDto로 파싱합니다.
                        val chatMessageDto = gson.fromJson(
                            jsonString,
                            ChatMessageDto::class.java
                        )

                        Log.d("StompService", "json 파싱 : ${chatMessageDto}")


//                         2. 파싱된 DTO를 UI가 알아볼 수 있는 UiChatMessage으로 변환합니다.
                        val chatmessage = chatMessageDto.toDomainModel(myUserId.value)

                        // 3. UI 상태를 업데이트하여 새 메시지를 목록의 맨 위에 추가합니다.
                        _uiState.update { currentState ->
                            currentState.copy(messages = listOf(chatmessage) + currentState.messages)
                        }

                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "STOMP 메시지 파싱 실패: $jsonString", e)
                    }
                }

            } finally {
                // stomp 소켓 연결 해제
                Log.d("StompService", "1 ViewModel cleared. Disconnecting STOMP...")
                chatRepository.disconnectStomp()
                Log.d("StompService", "2 ViewModel cleared. Disconnecting STOMP...")
            }
        }
    }


    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 함수들 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    //  과거 메시지를 불러오는 함수
    private suspend fun loadInitialMessages() {
//        viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        // Repository의 getMessageList 함수를 호출합니다.
        when (val result = chatRepository.getMessageList(chatRoomId, page = 0)) {
            is ApiResult.Success -> {
                val messageDtos = result.data.data?.messages ?: emptyList()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // DTO 리스트를 UI 모델 리스트로 변환하여 저장
                        messages = messageDtos.map { dto -> dto.toDomainModel(myUserId.value) }
                    )
                }
            }

            is ApiResult.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.errorBody?.message)
                }
            }

            is ApiResult.Exception -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.e.message)
                }
            }
        }
    }

    // 채팅 전송 함수 (기존과 거의 동일)
    fun sendMessage(text: String) {
        if (text.isBlank() || chatRoomId == -1L) return
        viewModelScope.launch {

            // 👇 1. 먼저 현재 내 ID를 가져옵니다. .value를 사용하는 것이 핵심입니다.
            val currentMyId = myUserId.value
            if (currentMyId == null) {
                Log.e("ChatRoomViewModel", "내 ID를 알 수 없어 메시지를 보낼 수 없습니다.")
                return@launch
            }

            // 채팅 보내기
            when (val result = chatRepository.sendMessage(chatRoomId, text)) {
                is ApiResult.Success -> {
                    Log.d("ChatRoomViewModel", "메시지 전송 성공")


                    // 👇 2. 내가 방금 보낸 메시지를 표현하는 ChatMessage 객체를 직접 만듭니다.
                    val newMyMessage = ChatMessage(
                        messageId = System.currentTimeMillis(), // 임시 ID, 실제 ID는 서버가 부여
                        chatRoomId = chatRoomId,
                        senderId = currentMyId,
                        senderNickname = "나", // TODO: 실제 내 닉네임 정보로 교체
                        senderProfileUrl = null, // TODO: 실제 내 프로필 URL로 교체
                        content = text, // 사용자가 입력한 텍스트
                        imageUrl = null,
                        // 👇 타임스탬프는 간단하게 "방금 전"으로 표시하거나, 실제 시간을 생성할 수 있습니다.
                        timestamp = "방금 전",
                        isFromMe = true // 내가 보낸 메시지이므로 항상 true
                    )


                    // 👇 3. 새로 만든 메시지 객체를 기존 메시지 목록의 맨 앞에 추가하여 UI 상태를 업데이트합니다.
                    _uiState.update { currentState ->
                        currentState.copy(messages = listOf(newMyMessage) + currentState.messages)
                    }

                }

                is ApiResult.Error -> Log.e("ChatRoomViewModel", "메시지 전송 API 에러")
                is ApiResult.Exception -> Log.e("ChatRoomViewModel", "메시지 전송 네트워크 예외")
            }
        }
    }


    /**
     * 이 ViewModel이 소멸되기 직전에 호출됩니다.
     * 채팅방 화면을 벗어날 때 STOMP 연결을 안전하게 해제하여 리소스 누수를 방지합니다.
     */
    override fun onCleared() {
        Log.d("StompService", "onCleared 호출 시작")
        super.onCleared()
        Log.d("StompService", "onCleared 호출 끝")
    }
}


// 과거 메시지(HTTP) DTO를 ChatMessage 모델로 변환
private fun MessageDto.toDomainModel(myUserId: Int?): ChatMessage {
    return ChatMessage(
        messageId = this.messageId,
        chatRoomId = this.chatId,
        senderId = this.sender.userId,
        senderNickname = this.sender.nickname,
        senderProfileUrl = this.sender.profileImage,
        content = this.content ?: "",
        imageUrl = this.imageUrl,
        timestamp = this.sentAt,
        isFromMe = this.sender.userId == myUserId,
//        isFromMe = true

    )
}

// 실시간 메시지(STOMP) DTO를 ChatMessage 모델로 변환
private fun ChatMessageDto.toDomainModel(myUserId: Int?): ChatMessage {
    return ChatMessage(
        messageId = this.messageId,
        chatRoomId = this.chatId,
        senderId = this.sender.userId,
        senderNickname = this.sender.nickname,
        senderProfileUrl = this.sender.profileImage,
        content = this.content ?: "",
        imageUrl = this.imageUrl,
        timestamp = this.sentAt,
        isFromMe = this.sender.userId == myUserId // 👇 isFromMe는 UI에서 myUserId와 senderId를 비교하여 결정해야 합니다.
    )
}








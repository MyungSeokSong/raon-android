package com.example.raon.features.chat.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.chat.data.remote.ChatMessageDto
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.dto.MessageDto // 👈 DTO import 변경
import com.example.raon.features.chat.domain.repository.ChatRepository
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI에서 사용할 데이터 모델
data class UiChatMessage(
    val content: String,
    val timestamp: String,
    val isFromMe: Boolean
)

// ▼▼▼ 1. UI 상태에 로딩과 에러 상태를 추가합니다. ▼▼▼
data class ChatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val messages: List<UiChatMessage> = emptyList()
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository, // 👈 DataStore 대신 UserRepository를 주입
    private val stompService: StompService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _myUserId = MutableStateFlow<Long?>(null)
    val myUserId2 = _myUserId.asStateFlow() // 외부에 공개할 때는 StateFlow로

    private val chatRoomId: Long = savedStateHandle.get<String>("chatRoomId")?.toLongOrNull() ?: -1L

    // ViewModel이 UI 상태를 직접 관리하도록 MutableStateFlow로 변경합니다.
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (chatRoomId != -1L) {
            loadInitialData()
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "유효하지 않은 채팅방입니다.") }
        }
    }


    init {

        Log.d("채팅프로세스2", "ChatRoomViewModel ->  채팅방 ID: ${chatRoomId}")


        // ViewModel이 생성될 때 UserRepository에서 사용자 프로필을 가져옴
        viewModelScope.launch {
            userRepository.getUserProfile()
                .collect { user -> // Flow<User?>를 수집
                    // user 객체가 null이 아닐 경우 userId를 상태에 업데이트
//                    _myUserId.value = user?.userId
                    _myUserId.value = user?.userId?.toLongOrNull() // 👈 .toLongOrNull() 추가

                }
        }
    }


    // 현재 로그인한 사용자 ID (임시)
    private val myUserId: Long = 1L

    init {
        Log.d("ChatRoomViewModel", "채팅방 ID : $chatRoomId")
        if (chatRoomId != -1L) {
            loadInitialMessages()   // 과거 채팅 메시지 가져오기

            connectStomp(chatRoomId)    // 실시간 채팅 stomp 연결


//            // 4. 실시간 메시지 수신을 시작합니다.
//            chatRepository.observeMessages().collect { messageDto ->
//                val newUiMessage = messageDto.toUiChatMessageStomp(myUserId)
//                _uiState.update { currentState ->
//                    currentState.copy(messages = listOf(newUiMessage) + currentState.messages)
//                }
//            }

        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "유효하지 않은 채팅방입니다.") }
        }


        // 실시간 메시지 Flow 수집: _uiState에 추가

        viewModelScope.launch {
            stompService.messages.collect { chatMessage ->
                _uiState.update {
                    it.copy(
                        messages = listOf(
                            chatMessage.toUiChatMessageStomp(myUserId) // ⬅️ DTO → UI 변환
                        ) + it.messages
                    )
                }
            }
        }
    }


    private fun loadInitialData() {
        // 👇👇👇 모든 비동기 작업은 이 viewModelScope.launch 블록 안에서 시작해야 합니다.
        viewModelScope.launch {
            // 1. 내 정보와 토큰을 먼저 가져옵니다.
//            val accessToken = authRepository.getAccessToken().first()
            // TODO: AuthRepository에 getUserId() 함수를 실제로 구현해야 합니다.
            // myUserId = authRepository.getUserId().first()

            // 2. 토큰으로 STOMP 연결을 시도합니다.
//            if (!accessToken.isNullOrBlank()) {
//                chatRepository.connectStomp(chatRoomId, accessToken)
//            } else {
//                Log.e("ChatViewModel", "Token is null, STOMP connection failed.")
//                return@launch
//            }

            // 3. 과거 메시지를 불러옵니다. (suspend 함수이므로 코루틴 안에서 호출)
            loadInitialMessages()

            // 4. 실시간 메시지 수신을 시작합니다. (collect는 suspend 함수이므로 코루틴 안에서 호출)
            chatRepository.observeMessages().collect { messageDto ->
                val newUiMessage = messageDto.toUiChatMessageStomp(myUserId)
                _uiState.update { currentState ->
                    currentState.copy(messages = listOf(newUiMessage) + currentState.messages)
                }
            }
        }
    }


    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 함수들 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


    //  과거 메시지를 불러오는 함수
    private fun loadInitialMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Repository의 getMessageList 함수를 호출합니다.
            when (val result = chatRepository.getMessageList(chatRoomId, page = 0)) {
                is ApiResult.Success -> {
                    val messageDtos = result.data.data?.messages ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // DTO 리스트를 UI 모델 리스트로 변환하여 저장
                            messages = messageDtos.map { dto -> dto.toUiChatMessage(myUserId) }
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
    }

    // 채팅 전송 함수 (기존과 거의 동일)
    fun sendMessage(text: String) {
        if (text.isBlank() || chatRoomId == -1L) return
        viewModelScope.launch {
            when (val result = chatRepository.sendMessage(chatRoomId, text)) {
                is ApiResult.Success -> {
                    Log.d("ChatRoomViewModel", "메시지 전송 성공")
                    // TODO: 메시지 전송 성공 시, 과거 메시지 목록을 다시 불러오거나
                    //       실시간 수신 로직을 통해 새 메시지가 반영되도록 해야 합니다.
                }

                is ApiResult.Error -> Log.e("ChatRoomViewModel", "메시지 전송 API 에러")
                is ApiResult.Exception -> Log.e("ChatRoomViewModel", "메시지 전송 네트워크 예외")
            }
        }
    }


    // Stomp 연결 진입 함수 (채팅방 ID로 구독 시작)
    private fun connectStomp(chatRoomId: Long) {
        viewModelScope.launch {
            stompService.connectAndSubscribe(chatRoomId)
        }
    }


    /**
     * 이 ViewModel이 소멸되기 직전에 호출됩니다.
     * 채팅방 화면을 벗어날 때 STOMP 연결을 안전하게 해제하여 리소스 누수를 방지합니다.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            Log.d("StompService", "ViewModel cleared. Disconnecting STOMP...")
            chatRepository.disconnectStomp()
        }
    }


}


// ▼▼▼ 5. MessageDto를 UiChatMessage으로 변환하는 새로운 확장 함수를 만듭니다. ▼▼▼
private fun MessageDto.toUiChatMessage(currentUserId: Long): UiChatMessage {
    return UiChatMessage(
        content = this.content ?: "", // content가 null일 경우를 대비
        timestamp = this.sentAt, // TODO: 시간 포맷팅 필요
        isFromMe = this.sender.userId == currentUserId
    )
}

// 추가: 실시간 STOMP용
private fun ChatMessageDto.toUiChatMessageStomp(currentUserId: Long): UiChatMessage {
    return UiChatMessage(
        content = this.content ?: "",
        timestamp = this.timestamp,
        isFromMe = this.senderId == currentUserId
    )
}







package com.example.raon.features.chat.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.chat.domain.model.ChatMessage
import com.example.raon.features.chat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI에서 사용할 데이터 모델 (isFromMe 필드 추가)
data class UiChatMessage(
    val content: String,
    val timestamp: String,
    val senderNickname: String,
    val isFromMe: Boolean
)

// UI 전체 상태
data class ChatUiState(
    val messages: List<UiChatMessage> = emptyList(),
    val inputText: String = ""
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository, // Data 계층의 구현체가 아닌 Domain의 인터페이스를 주입받음
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    // navigation으로부터 실제 chatRoomId를 받아와서 저장
    // String으로 받은거 Long으로 바꿔주기


    val chatRoomId: Long = savedStateHandle.get<String>("chatRoomId")?.toLongOrNull() ?: -1L

    init { // 채팅방 ID 확인
        Log.d("ChatRoomViewModel", "채팅방 ID : ${chatRoomId}")
    }

    // 현재 로그인한 사용자 ID (임시)
    private val myUserId: Long = 1L


    // Repository로부터 메시지 Flow를 받아 UI용 StateFlow로 변환
    val uiState: StateFlow<ChatUiState> =
        chatRepository.getMessages(chatRoomId = chatRoomId)
            .map { domainMessages ->
                val uiMessages = domainMessages.map { it.toUiChatMessage(myUserId) }
                ChatUiState(messages = uiMessages.reversed()) // UI에서는 최신 메시지가 아래에 오도록 순서 변경
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ChatUiState() // 초기 상태
            )

    // TODO: 입력 텍스트 상태 관리 (별도 StateFlow 또는 uiState에 포함)
    // var inputText by mutableStateOf("")


    // 채팅 보내는 함수 Viewmodel -> Repository
    fun sendMessage(text: String) {
        if (text.isBlank() || chatRoomId == -1L) return
        viewModelScope.launch {
            // ▼▼▼ 5. sendMessage 호출 시 실제 chatId를 사용하고, 반환된 ApiResult를 처리 ▼▼▼
            when (val result = chatRepository.sendMessage(chatRoomId, text)) {
                is ApiResult.Success -> {
                    // 메시지 전송에 성공했을 때의 처리
                    println("메시지 전송 성공: ${result.data.data?.content}")
                    Log.d("ChatRoomViewModel", "메시지 전송 성공")
                    // TODO: 입력창 비우기 등 UI 상태 업데이트
                }

                is ApiResult.Error -> {
                    // API 에러가 발생했을 때의 처리
                    println("메시지 전송 실패: ${result.errorBody?.message}")
                    Log.d("ChatRoomViewModel", "메시지 전송 실패")

                    // TODO: 사용자에게 에러 메시지 보여주기 (Toast 등)
                }

                is ApiResult.Exception -> {
                    // 네트워크 예외 등 그 외 에러 처리
                    println("메시지 전송 오류: ${result.e.message}")
                    Log.d("ChatRoomViewModel", "메시지 전송 오류")

                    // TODO: 사용자에게 에러 메시지 보여주기 (Toast 등)
                }
            }
        }
    }
}

// 도메인 모델을 UI 모델로 변환하는 확장 함수
private fun ChatMessage.toUiChatMessage(currentUserId: Long): UiChatMessage {
    return UiChatMessage(
        content = this.content,
        timestamp = this.timestamp,
        senderNickname = this.senderNickname,
        isFromMe = this.senderId == currentUserId
    )
}
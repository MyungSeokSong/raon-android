package com.example.raon.features.chat.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.dto.ChatMessageDto
import com.example.raon.features.chat.data.remote.dto.MessageDto
import com.example.raon.features.chat.data.remote.dto.ai.FraudDetectionRequestDto
import com.example.raon.features.chat.data.remote.dto.ai.MessageInFraudRequestDto
import com.example.raon.features.chat.domain.model.ChatMessage
import com.example.raon.features.chat.domain.repository.ChatRepository
import com.example.raon.features.user.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val fraudWarningMessage: String? = null
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val stompService: StompService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatRoomId: Long = savedStateHandle.get<String>("chatRoomId")?.toLongOrNull() ?: -1L
    private val gson = Gson()
    private val _myUserId = MutableStateFlow<Int?>(null)
    val myUserId = _myUserId.asStateFlow()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _myUserId.value = userRepository.getUserProfile().first()?.userId
            if (chatRoomId != -1L) {
                loadInitialDataAndConnect()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "유효하지 않은 채팅방입니다.") }
            }
        }
    }

    private fun loadInitialDataAndConnect() {
        viewModelScope.launch {
            try {
                chatRepository.connectStomp(chatRoomId = chatRoomId)
                loadInitialMessages()
                chatRepository.observeMessages(chatRoomId).collect { jsonString ->
                    try {
                        val chatMessageDto = gson.fromJson(jsonString, ChatMessageDto::class.java)
                        val chatMessage = chatMessageDto.toDomainModel(myUserId.value)
                        _uiState.update { currentState ->
                            currentState.copy(messages = listOf(chatMessage) + currentState.messages)
                        }
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "STOMP 메시지 파싱 실패: $jsonString", e)
                    }
                }
            } finally {
                chatRepository.disconnectStomp()
            }
        }
    }

    // 👇 [핵심 수정] DTO 리스트를 UI 모델 리스트로 변환하는 .map 코드를 추가합니다.
    private suspend fun loadInitialMessages() {
        _uiState.update { it.copy(isLoading = true) }
        when (val result = chatRepository.getMessageList(chatRoomId, page = 0)) {
            is ApiResult.Success -> {
                val messageDtos = result.data.data?.messages ?: emptyList()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // DTO 리스트(messageDtos)를 UI 모델 리스트(ChatMessage)로 변환
                        messages = messageDtos.map { dto -> dto.toDomainModel(myUserId.value) }
                    )
                }
            }

            is ApiResult.Error -> _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.errorBody?.message
                )
            }

            is ApiResult.Exception -> _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.e.message
                )
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || chatRoomId == -1L) return
        viewModelScope.launch {
            val currentMyId = myUserId.value ?: return@launch
            when (chatRepository.sendMessage(chatRoomId, text)) {
                is ApiResult.Success -> Log.d("ChatViewModel", "메시지 전송 성공")
                is ApiResult.Error -> Log.e("ChatViewModel", "메시지 전송 API 에러")
                is ApiResult.Exception -> Log.e("ChatViewModel", "메시지 전송 네트워크 예외")
            }
        }
    }

    fun detectFraud() {
        viewModelScope.launch {
            val currentMyId = myUserId.value ?: return@launch
            val currentMessages = _uiState.value.messages
            if (currentMessages.isEmpty()) return@launch

            val messageDtos = currentMessages.map { chatMessage ->
                MessageInFraudRequestDto(
                    messageId = chatMessage.messageId,
                    senderId = chatMessage.senderId,
                    content = chatMessage.content,
                    sentAt = chatMessage.timestamp
                )
            }
            val requestDto = FraudDetectionRequestDto(
                requesterId = currentMyId,
                messages = messageDtos.reversed()
            )

            when (val result = chatRepository.detectFraud(chatRoomId, requestDto)) {
                is ApiResult.Success -> {
                    val analysisResult = result.data.data
                    if (analysisResult != null && (analysisResult.result == "SAFE" || analysisResult.result == "WARNING" || analysisResult.result == "DANGER")) {
                        _uiState.update { it.copy(fraudWarningMessage = analysisResult.message) }
                    } else {
                        _uiState.update { it.copy(fraudWarningMessage = null) }
                    }
                }

                is ApiResult.Error -> _uiState.update { it.copy(fraudWarningMessage = "분석 중 오류가 발생했습니다.") }
                is ApiResult.Exception -> _uiState.update { it.copy(fraudWarningMessage = "네트워크 오류로 분석에 실패했습니다.") }
            }
        }
    }

    fun closeWarningBanner() {
        _uiState.update { it.copy(fraudWarningMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            chatRepository.disconnectStomp()
        }
    }
}

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
    )
}

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
        isFromMe = this.sender.userId == myUserId
    )
}
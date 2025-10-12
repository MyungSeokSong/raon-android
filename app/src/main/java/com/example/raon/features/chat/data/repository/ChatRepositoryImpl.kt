package com.example.raon.features.chat.data.repository

// import com.example.raon.features.chat.data.remote.api.ChatApiService // 실제 ApiService
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.core.network.handleApi
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.api.ChatApiService
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageRequestDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import com.example.raon.features.chat.domain.model.ChatMessage
import com.example.raon.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * ChatRepository의 실제 구현체.
 * 실제 데이터 소스(Remote API, Local DB)와 통신합니다.
 */
class ChatRepositoryImpl @Inject constructor(
    // private val chatApiService: ChatApiService // Hilt/Koin 등으로 실제 API 서비스를 주입받습니다.
    private val chatApiService: ChatApiService,  //
    private val stompService: StompService // 실시간 채팅을 위한 StompService

) : ChatRepository {

    // ▼▼▼ 1. HTTP GET으로 과거 메시지를 불러오는 실제 구현 ▼▼▼
    override suspend fun getMessageList(
        chatId: Long,
        page: Int
    ): ApiResult<ApiResponse<MessageListDto>> {
        return handleApi { chatApiService.getMessages(chatId, page) }
    }


    // 임시 더미 데이터를 사용한 예시
    override fun getMessages(chatRoomId: Long): Flow<List<ChatMessage>> = flow {
        // 실제로는 WebSocket이나 API를 통해 메시지를 수신하는 로직이 들어갑니다.
        // 여기서는 1초마다 더미 데이터를 방출하는 예시를 보여줍니다.
        val dummyHistory = listOf(
            ChatMessage(1L, chatRoomId, 2L, "상대방", null, "안녕하세요", null, "오후 2:30", true),
            ChatMessage(2L, chatRoomId, 1L, "나", null, "네 안녕하세요!", null, "오후 2:31", false)
        )
        emit(dummyHistory) // 초기 메시지 전송
        delay(1000)
        emit(
            dummyHistory + ChatMessage(
                3L,
                chatRoomId,
                2L,
                "상대방",
                null,
                "혹시 네고 가능한가요?",
                null,
                "오후 2:31",
                true
            )
        )
    }


    // 서버로 채팅을 보내는 Repository 함수
    override suspend fun sendMessage(
        chatRoomId: Long,
        chatMessage: String
    ): ApiResult<ApiResponse<SendMessageResponseDto>> {

        // 서버에 보낼 요청 DTO
        val requestDto = SendMessageRequestDto(content = chatMessage)

        // 2. ApiService를 호출하고, handleApi로 감싼 결과를 그대로 반환(return)합니다.
        //    (결과를 여기서 처리하지 않고 ViewModel로 넘겨주는 것이 핵심입니다.)
        return handleApi { chatApiService.sendMessage(chatRoomId, requestDto) }
    }


    override suspend fun getChatRoomList(page: Int): ApiResult<ApiResponse<ChatRoomListDto>> {
        return handleApi { chatApiService.getChats(page) }
    }

    // --- STOMP 관련 함수 구현 ---

    override suspend fun connectStomp(chatRoomId: Long) {
        // StompService에 작업을 위임합니다.
        stompService.connectAndSubscribe(chatRoomId)
    }

    override fun observeMessages(chatId: Long): Flow<String> {
        // StompService가 제공하는 메시지 Flow를 그대로 반환합니다.
        return stompService.messages
    }

//    override suspend fun sendStompMessage(chatRoomId: Long, message: String) {
//        // StompService에 작업을 위임합니다.
//        stompService.sendMessage(chatRoomId, message)
//    }

    override suspend fun disconnectStomp() {
        // StompService에 작업을 위임합니다.
        stompService.disconnect()
    }
}
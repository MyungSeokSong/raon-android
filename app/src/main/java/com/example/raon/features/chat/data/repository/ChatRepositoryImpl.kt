package com.example.raon.features.chat.data.repository

// import com.example.raon.features.chat.data.remote.api.ChatApiService // 실제 ApiService
import android.util.Log
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.core.network.handleApi
import com.example.raon.features.chat.data.remote.StompService
import com.example.raon.features.chat.data.remote.api.ChatApiService
import com.example.raon.features.chat.data.remote.dto.ChatRoomDetailResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageRequestDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import com.example.raon.features.chat.data.remote.dto.ai.FraudData
import com.example.raon.features.chat.data.remote.dto.ai.FraudDetectionRequestDto
import com.example.raon.features.chat.data.remote.dto.ai.ImageAnalysisResponseDto
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
            ChatMessage(1L, chatRoomId, 2, "상대방", null, "안녕하세요", null, "오후 2:30", true, "11"),
            ChatMessage(2L, chatRoomId, 1, "나", null, "네 안녕하세요!", null, "오후 2:31", false, "11")
        )
        emit(dummyHistory) // 초기 메시지 전송
        delay(1000)
        emit(
            dummyHistory + ChatMessage(
                3L,
                chatRoomId,
                2,
                "상대방",
                null,
                "혹시 네고 가능한가요?",
                null,
                "오후 2:31",
                true,
                "11"
            )
        )
    }


    // 서버에 get chat 요청을 보냄 -> 채팅방 관련 상세 데이터를 줌
    override suspend fun getChatRoomDetails(chatId: Long): ApiResult<ChatRoomDetailResponse> {
        Log.d("ChatRepository_getChat", "🚀 Fetching chat room details for chatId: $chatId")
        // handleApi를 사용하여 API 호출 및 결과 처리
        val result = handleApi { chatApiService.getChatRoomDetails(chatId) }
        Log.d("ChatRepository_getChat", "✅ Chat room details result: $result")
        return result
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


    override suspend fun disconnectStomp() {
        // StompService에 작업을 위임합니다.
        stompService.disconnect()
    }


    /**
     * 사기 탐지 API 호출의 실제 구현
     */
    override suspend fun detectFraud(
        userId: Long,
        request: FraudDetectionRequestDto // 👇 파라미터를 DTO로 변경
    ): ApiResult<ApiResponse<FraudData>> {

        // [로그 1] 함수가 호출되었는지, 어떤 chatRoomId를 서버로 보낼지 확인
        Log.d("ChatRepo_Fraud", "🚀 detectFraud called with userId: $userId")

        Log.d("ChatRepo_Fraud", "🚀 detectFraud called with request: $request")


        // API 서비스를 호출하고 결과를 변수에 저장합니다.
        val result = handleApi {
            chatApiService.detectFraud(userId, request)
            // 만약 DTO를 보낸다면: chatApiService.detectFraud(request)
        }

        // [로그 2] 서버로부터 받은 최종 결과가 Success인지 Error인지, 데이터는 무엇인지 확인
        Log.d("ChatRepo_Fraud", "✅ Response received: $result")

        // 최종 결과를 ViewModel로 반환합니다.
        return result
    }


    // [추가] AI 이미지 분석 구현
    override suspend fun analyzeImages(chatRoomId: Long): ApiResult<ApiResponse<ImageAnalysisResponseDto>> {
        Log.d("ChatRepo_Image", "🚀 Requesting image analysis for chat: $chatRoomId")
        // handleApi를 사용하여 API 호출 및 결과 처리
        val result = handleApi { chatApiService.analyzeImages(chatRoomId) }
        Log.d("ChatRepo_Image", "✅ Image analysis response: $result")
        return result
    }


    // [ 메시지 읽음 처리 함수 ]
    override suspend fun markMessagesAsRead(chatId: Long): ApiResult<ApiResponse<Unit>> {
        Log.d("ChatRepository", "🚀 Mark messages as read for chatId: $chatId")
        val result = handleApi { chatApiService.markMessagesAsRead(chatId) }
        Log.d("ChatRepository", "✅ Mark messages as read result: $result")
        return result
    }

}
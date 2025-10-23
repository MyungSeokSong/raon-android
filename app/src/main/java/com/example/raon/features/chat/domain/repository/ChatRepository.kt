package com.example.raon.features.chat.domain.repository

//import com.example.raon.features.chat.data.remote.dto.ChatRoomListDTO
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomDetailResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import com.example.raon.features.chat.data.remote.dto.ai.FraudData
import com.example.raon.features.chat.data.remote.dto.ai.FraudDetectionRequestDto
import com.example.raon.features.chat.data.remote.dto.ai.ImageAnalysisResponseDto
import com.example.raon.features.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow


// 채팅 데이터에 접근하기 위한 인터페이스
interface ChatRepository {


    //서버로 메시지를 전송합니다.
    suspend fun sendMessage(
        chatRoomId: Long,   // 채팅방 번호
        chatMessage: String // 채팅 메시지
    ): ApiResult<ApiResponse<SendMessageResponseDto>>


    // 서버에서 채팅방 리스트를 가여오는 함수
    suspend fun getChatRoomList(page: Int): ApiResult<ApiResponse<ChatRoomListDto>>


    suspend fun getChatRoomDetails(chatId: Long): ApiResult<ChatRoomDetailResponse>


    // HTTP로 과거 채팅 메시지를 가져오는 함수
    suspend fun getMessageList(chatId: Long, page: Int): ApiResult<ApiResponse<MessageListDto>>


    // 채팅방의 과거 메시지 목록 가져오기 (for ChatRoomScreen)
    // @return ChatMessage 리스트를 방출하는 Flow
    fun getMessages(chatRoomId: Long): Flow<List<ChatMessage>>


    // --- STOMP 실시간 채팅을 위한 함수들 추가 ---
    /**
     * STOMP 세션을 연결하고 특정 채팅방의 메시지를 구독합니다.
     * @param chatRoomId 구독할 채팅방 ID
     * @param authToken 인증을 위한 Access Token
     */
    suspend fun connectStomp(chatRoomId: Long)

    /**
     * 구독 중인 STOMP 메시지 흐름(Flow)을 제공합니다.
     */
    fun observeMessages(chatId: Long): Flow<String>


    /**
     * STOMP를 통해 실시간 메시지를 전송합니다.
     * @param recipientId 메시지를 받을 상대방의 사용자 ID
     * @param message 보낼 메시지 내용
     */
//    suspend fun sendStompMessage(chatRoomId: Long, message: String)


    // [ Stomp 세션 연결 해제 ]
    suspend fun disconnectStomp()


    // [ ai 채팅 사기 탐지 함수 ]
    suspend fun detectFraud(
        userId: Long,
        request: FraudDetectionRequestDto
    ): ApiResult<ApiResponse<FraudData>> // <--- ✅ 'ApiResult'로 감싸주세요.


    // [ AI 이미지 분석 함수 ]
    suspend fun analyzeImages(chatRoomId: Long): ApiResult<ApiResponse<ImageAnalysisResponseDto>>


    // [ 메시지 읽음 처리 함수]
    suspend fun markMessagesAsRead(chatId: Long): ApiResult<ApiResponse<Unit>>

}
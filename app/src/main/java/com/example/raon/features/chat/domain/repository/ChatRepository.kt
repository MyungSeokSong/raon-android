package com.example.raon.features.chat.domain.repository

//import com.example.raon.features.chat.data.remote.dto.ChatRoomListDTO
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
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
    suspend fun connectStomp(chatRoomId: Long, authToken: String)

    /**
     * 구독 중인 STOMP 메시지 흐름(Flow)을 제공합니다.
     * @return ChatMessageDto 객체를 방출하는 Flow
     */
    fun observeMessages(): Flow<com.example.raon.features.chat.data.remote.ChatMessageDto>

    /**
     * STOMP를 통해 실시간 메시지를 전송합니다.
     * @param recipientId 메시지를 받을 상대방의 사용자 ID
     * @param message 보낼 메시지 내용
     */
    suspend fun sendStompMessage(chatRoomId: Long, message: String)

    /**
     * STOMP 세션 연결을 해제합니다.
     */
    suspend fun disconnectStomp()

    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 보류 코드 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


    // 특정 채팅방의 상세 정보(참여자, 상품 등)를 가져오기(for ChatRoomScreen)
//    suspend fun getChatRoomInfo(chatId: Long): ApiResult<ApiResponse<ChatRoomInfoData>>


    /**
     * 특정 채팅방의 새 메시지를 실시간(WebSocket)으로 구독합니다. (for ChatRoomScreen)
     * 역할 구분을 위해 이름을 observeMessages로 변경하는 것을 추천합니다.
     */
//    fun observeMessages(chatId: Long): Flow<ChatMessage>


    // TODO: 소켓 연결, 해제 등 필요한 다른 함수들을 정의할 수 있습니다.
}
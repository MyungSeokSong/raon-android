package com.example.raon.features.chat.domain.repository

import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import com.example.raon.features.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * 채팅 데이터에 접근하기 위한 인터페이스 (규칙)
 */
interface ChatRepository {

    /**
     * 특정 채팅방의 메시지 목록을 실시간으로 가져옵니다.
     * @param roomId 채팅방 ID
     * @return ChatMessage 리스트를 방출하는 Flow
     */
    fun getMessages(chatRoomId: Long): Flow<List<ChatMessage>>

    /**
     * 서버로 메시지를 전송합니다.
     * @param roomId 보낼 채팅방 ID
     * @param content 보낼 메시지 내용
     */
    suspend fun sendMessage(
        chatRoomId: Long,
        chatMessage: String
    ): ApiResult<ApiResponse<SendMessageResponseDto>>


    // TODO: 소켓 연결, 해제 등 필요한 다른 함수들을 정의할 수 있습니다.
}
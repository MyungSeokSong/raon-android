package com.example.raon.features.chat.data.remote.api

import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.SendMessageRequestDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatApiService {

    // 채팅 메시지 전송 api
    @POST("/api/v1/chats/{chatRoomId}/messages")
    suspend fun sendMessage(
        @Path("chatRoomId") chatRoomId: Long,
        @Body reponsebody: SendMessageRequestDto
    ): Response<ApiResponse<SendMessageResponseDto>>


}
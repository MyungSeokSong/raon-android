package com.example.raon.features.chat.data.remote.api

import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageRequestDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ChatApiService {

    // 채팅 메시지 전송 api
    @POST("/api/v1/chats/{chatRoomId}/messages")
    suspend fun sendMessage(
        @Path("chatRoomId") chatRoomId: Long,
        @Body reponsebody: SendMessageRequestDto
    ): Response<ApiResponse<SendMessageResponseDto>>


    // 채팅방 리스트 가져오는 api
    @GET("/api/v1/chats")
    suspend fun getChats(
        @Query("page") page: Int
    ): Response<ApiResponse<ChatRoomListDto>>


    // 특정 채팅방의 과거 저장된 Messages를 가져오는 api
    @GET("/api/v1/chats/{chatRoomId}/messages")
    suspend fun getMessages(
        @Path("chatRoomId") chatId: Long,
        @Query("page") page: Int
    ): Response<ApiResponse<MessageListDto>>

}
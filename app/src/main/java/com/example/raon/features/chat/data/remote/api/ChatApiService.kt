package com.example.raon.features.chat.data.remote.api

import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomDetailResponse
import com.example.raon.features.chat.data.remote.dto.ChatRoomListDto
import com.example.raon.features.chat.data.remote.dto.MessageListDto
import com.example.raon.features.chat.data.remote.dto.SendMessageRequestDto
import com.example.raon.features.chat.data.remote.dto.SendMessageResponseDto
import com.example.raon.features.chat.data.remote.dto.ai.FraudData
import com.example.raon.features.chat.data.remote.dto.ai.FraudDetectionRequestDto
import com.example.raon.features.chat.data.remote.dto.ai.ImageAnalysisResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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


    //  채팅방 상세 정보 가져오는 GET API 추가
    @GET("/api/v1/chats/{chatId}") // Postman 스크린샷 참고
    suspend fun getChatRoomDetails(
        @Path("chatId") chatId: Long // 경로 변수로 채팅방 ID 받기
    ): Response<ChatRoomDetailResponse> // 새로 만든 전체 응답 DTO 사용
    //  완료


    // 특정 채팅방의 과거 저장된 Messages를 가져오는 api
    @GET("/api/v1/chats/{chatRoomId}/messages")
    suspend fun getMessages(
        @Path("chatRoomId") chatId: Long,
        @Query("page") page: Int
    ): Response<ApiResponse<MessageListDto>>


    // Postman 이미지의 POST 요청을 정의합니다.
    // {{base_url}}/api/v1/chats/1/fraud-detection
    // 여기서 '1'은 채팅방 ID로 보이므로, @Path를 사용해 동적으로 변경할 수 있게 합니다.
    @POST("api/v1/chats/{userId}/fraud-detection")
    suspend fun detectFraud(
        @Path("userId") userId: Long,

        @Body request: FraudDetectionRequestDto // Body에 보내줄 ChatMessage
    ): Response<ApiResponse<FraudData>> // 1단계에서 만든 데이터 클래스로 응답을 받음


    // [추가] AI 이미지 분석 API
    @POST("/api/v1/chats/{chatRoomId}/image-analysis")
    suspend fun analyzeImages(
        @Path("chatRoomId") chatRoomId: Long
    ): Response<ApiResponse<ImageAnalysisResponseDto>> // 1번에서 만든 DTO 사용


    // [ 메시지 읽음 처리 API ]
    @PUT("/api/v1/chats/{chatId}/messages/read")
    suspend fun markMessagesAsRead(
        @Path("chatId") chatId: Long
    ): Response<ApiResponse<Unit>> // 응답 본문에 특별한 데이터가 없으므로 Unit 사용

}
package com.example.raon.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName


// ▼▼▼ POST /items/{itemId}/chats 에 대한 응답 DTO 추가 ▼▼▼
data class CreateChatRoomResponseDto(
    @SerializedName("data") val data: ChatIdDto
)

// 공통으로 사용되는 chatId 데이터
data class ChatIdDto(
    @SerializedName("chatId") val chatId: Long
)
package com.example.raon.features.chat.data.remote.dto


import com.google.gson.annotations.SerializedName


// GET /api/v1/chats/{chatId}/messages API의 응답 DTO


// 응답의 "data" 필드 구조 (메시지 목록 + 페이지 정보)
data class MessageListDto(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long
)

// "messages" 리스트의 각 아이템 구조
data class MessageDto(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("sender") val sender: SenderData2,
    @SerializedName("content") val content: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("isDeleted") val isDeleted: Boolean,
    @SerializedName("sentAt") val sentAt: String
)

// "sender" 필드의 구조 (다른 DTO 파일에 이미 있다면 재사용 가능)
data class SenderData2(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean
)

package com.example.raon.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

// 채팅 응답 Data Dto
data class SendMessageResponseDto(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("sender") val sender: SenderData,
    @SerializedName("content") val content: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("isDeleted") val isDeleted: Boolean,
    @SerializedName("sentAt") val sentAt: String
)

data class SenderData(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean
)
package com.example.raon.features.chat.data.remote.dto.ai


import com.google.gson.annotations.SerializedName

data class FraudDetectionRequestDto(
    @SerializedName("requesterId")
    val requesterId: Int,

    @SerializedName("messages")
    val messages: List<MessageInFraudRequestDto>
)

data class MessageInFraudRequestDto(
    @SerializedName("messageId")
    val messageId: Long,

    @SerializedName("senderId")
    val senderId: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("sentAt")
    val sentAt: String
)
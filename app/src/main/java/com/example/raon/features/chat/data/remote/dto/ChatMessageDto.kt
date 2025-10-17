package com.example.raon.features.chat.data.remote.dto


// 실시간 Stomp에서 사용할 Dto
data class ChatMessageDto(
    val messageId: Long,
    val chatId: Long,
    val sender: SenderDto,
    val content: String?,
    val imageUrl: String?,
    val isRead: Boolean,
    val isDeleted: Boolean,
    val sentAt: String
)

data class SenderDto(
    val userId: Int,
    val nickname: String,
    val profileImage: String?,
    val isDeleted: Boolean
)

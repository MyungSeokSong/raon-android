package com.example.raon.features.chat.domain.model


// 앱 전체(ViewModel, UI)에서 사용할 데이터 모델
data class ChatMessage(
    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val senderNickname: String,
    val senderProfileUrl: String?,
    val content: String,
    val imageUrl: String?,
    val timestamp: String,
    val isFromMe: Boolean
)

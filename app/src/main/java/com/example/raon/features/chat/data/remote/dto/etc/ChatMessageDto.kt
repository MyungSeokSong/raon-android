//package com.example.raon.features.chat.data.remote.dto.etc
//
//
//import com.example.raon.features.chat.domain.model.ChatMessage
//import com.google.gson.annotations.SerializedName
//
//// 서버와 직접 통신하는 DTO
//data class ChatMessageDto(
//    @SerializedName("messageId") val messageId: Long,
//    @SerializedName("chatId") val chatId: String,
//    @SerializedName("sender") val sender: SenderDto,
//    @SerializedName("content") val content: String,
//    @SerializedName("imageUrl") val imageUrl: String?,
//    @SerializedName("isRead") val isRead: Boolean,
//    @SerializedName("sentAt") val sentAt: String
//)
//
//data class SenderDto(
//    @SerializedName("userId") val userId: Long,
//    @SerializedName("nickname") val nickname: String,
//    @SerializedName("profileImage") val profileImage: String?
//)
//
//// DTO -> Domain Model 변환 함수
//fun ChatMessageDto.toDomainModel(): ChatMessage {
//    return ChatMessage(
//        messageId = this.messageId,
//        roomId = this.chatId,
//        senderId = this.sender.userId,
//        senderNickname = this.sender.nickname,
//        senderProfileUrl = this.sender.profileImage,
//        content = this.content,
//        imageUrl = this.imageUrl,
//        timestamp = this.sentAt
//    )
//}

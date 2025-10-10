package com.example.raon.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

// "data" 필드 내부의 구조 (페이징 정보 추가)
data class ChatRoomListDto(
    @SerializedName("chats") val chats: List<ChatRoomInfo>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long
)

// "chats" 리스트의 각 아이템 구조 (lastMessage 추가)
data class ChatRoomInfo(
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("product") val product: ProductInChatList,
    @SerializedName("buyer") val buyer: UserInChatList,
    @SerializedName("seller") val seller: UserInChatList,
    @SerializedName("lastMessage") val lastMessage: LastMessageInfo?, // 마지막 메시지는 없을 수도 있음(Nullable)
    @SerializedName("createdAt") val createdAt: String
)

// ▼▼▼ "lastMessage" 객체를 위한 새로운 data class 추가 ▼▼▼
data class LastMessageInfo(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("sentAt") val sentAt: String
)

// 채팅 목록 안의 상품 정보 (기존과 동일)
data class ProductInChatList(
    @SerializedName("productId") val productId: Long,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("title") val title: String,
    @SerializedName("status") val status: String
)

// 채팅 목록 안의 유저 정보 (기존과 동일)
data class UserInChatList(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?
)
package com.example.raon.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName


// 채팅방 정보 DTO
// GetChatResponseDto.kt
data class GetChatRoomResponseDto(
    @SerializedName("data") val data: ChatDataDto
)

data class ChatDataDto(
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("product") val product: ProductInChatDto,
    @SerializedName("seller") val seller: UserInChatDto
)

data class ProductInChatDto(
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String?
)

data class UserInChatDto(
    @SerializedName("nickname") val nickname: String
)
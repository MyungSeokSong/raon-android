package com.example.raon.features.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

// Post Body에 채팅 메시지를 담아서 보낼거임
// context -> 채팅 메시지를 담을 body-value
// POST /chats/{chatId}/messages 요청 시 Body에 담을 데이터

data class SendMessageRequestDto(

    @SerializedName("content") val content: String
)
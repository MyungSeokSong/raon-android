package com.example.raon.features.item.data.remote.dto.update_status


import com.google.gson.annotations.SerializedName


// Item 상태 업데이트 요청 DTO -> AVAILABLE, RESERVED, SOLD

data class UpdateStatusRequest(
    @SerializedName("status")
    val status: String,
    @SerializedName("buyerId")
    val buyerId: Int? = null // null을 보낼 수 있도록 nullable로 설정
)
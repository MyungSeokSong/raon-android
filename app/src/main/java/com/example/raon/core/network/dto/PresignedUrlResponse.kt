package com.example.raon.core.network.dto

import com.google.gson.annotations.SerializedName


// Presigned URL 요청 후 받을 응답 데이터
data class PresignedUrlResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("url")
    val url: String
)
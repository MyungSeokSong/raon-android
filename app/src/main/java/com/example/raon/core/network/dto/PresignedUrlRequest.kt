package com.example.raon.core.network.dto

import com.google.gson.annotations.SerializedName


// Presigned URL을 요청할 때 보낼 데이터
data class PresignedUrlRequest(
    @SerializedName("uploadType")
    val uploadType: String,
    @SerializedName("fileName")
    val fileName: String
)
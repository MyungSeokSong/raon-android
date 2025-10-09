package com.example.raon.core.network.model

import com.google.gson.annotations.SerializedName


// 서버의 에러 응답 JSON을 파싱하기 위한 DTO
data class ErrorResponseBody(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String
)
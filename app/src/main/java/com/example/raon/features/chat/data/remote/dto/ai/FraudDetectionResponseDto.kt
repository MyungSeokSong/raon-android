package com.example.raon.features.chat.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

// API 응답의 가장 바깥쪽 구조
data class FraudDetectionResponseDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: FraudData? // data가 null일 수도 있으니 ? 처리
)

// "data" 객체 내부 구조
data class FraudData(
    @SerializedName("result")
    val result: String, // "SAFE", "WARNING", "DANGER"
    @SerializedName("message")
    val message: String // "「상대」는 택배비를 요구하고..."
)
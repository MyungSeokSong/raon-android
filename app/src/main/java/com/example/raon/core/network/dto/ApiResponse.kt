package com.example.raon.core.network.dto

import com.google.gson.annotations.SerializedName


// 서버 응답의 최상위 구조와 일치하는 데이터 클래스
data class ApiResponse<T>(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)
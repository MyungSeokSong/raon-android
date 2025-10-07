package com.example.raon.core.network.dto


// 서버 응답의 최상위 구조와 일치하는 데이터 클래스
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?
)
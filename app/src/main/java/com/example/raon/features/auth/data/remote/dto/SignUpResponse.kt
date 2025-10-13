package com.example.raon.features.auth.data.remote.dto

data class SignUpResponse(
    val code: String,   // 로그인 확인
    val message: String,// 로그인 메세지
    val data: LoginData // 로그인 토큰 데이터
)

// 로그인 토큰 데이터
data class SignUpData(
    val accessToken: String,
    val accessTokenExpirationTime: Int
)


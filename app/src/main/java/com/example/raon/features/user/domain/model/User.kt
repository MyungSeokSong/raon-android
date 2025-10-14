package com.example.raon.features.user.domain.model


// UI 계층 등 앱 내부에서 실제로 사용할 깔끔한 데이터 모델
data class User(
    val userId: String,
    val nickname: String,
    val email: String,
    val profileImage: String?,
    val address: String,
    val locationId: Int
)
package com.example.raon.features.user.data.dto

// ME -> Get Profile
// 서버의 JSON 응답과 1:1로 매칭되는 DTO
data class ProfileResponse(
    val code: String,
    val message: String,
    val data: UserData
)

data class UserData(
    val userId: Int,
    val nickname: String,
    val email: String,
    val profileImage: String?, // null일 수 있으므로 Nullable
    val location: LocationData
)

data class LocationData(
    val locationId: Int,
    val address: String
)

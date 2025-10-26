package com.example.raon.features.item.data.remote.dto.update_status


import com.google.gson.annotations.SerializedName

/**
 * Get Buyers API의 전체 응답 구조
 * { "code": "OK", "message": "...", "data": { ... } }
 */
data class BuyerListResponseDto(
    @SerializedName("data")
    val data: BuyerListDataDto
)

/**
 * 응답의 "data" 객체에 해당하는 구조
 * { "users": [ ... ], "currentPage": 0, ... }
 */
data class BuyerListDataDto(
    @SerializedName("users")
    val users: List<BuyerDto>
)

/**
 * "users" 배열 안의 개별 구매자 정보 구조
 * { "userId": 5, "nickname": "홍길동4", "profileImage": null }
 */
data class BuyerDto(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String?
)
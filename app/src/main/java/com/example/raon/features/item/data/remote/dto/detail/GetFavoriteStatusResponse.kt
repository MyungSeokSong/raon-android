package com.example.raon.features.item.data.remote.dto.detail


import com.google.gson.annotations.SerializedName

// 1. 최상위 JSON 객체 전체를 나타내는 DTO
data class GetFavoriteStatusResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: FavoriteStatusDataDto // 2. "data" 객체를 위한 중첩 클래스
)

// 2. "data" 객체 내부를 나타내는 DTO
data class FavoriteStatusDataDto(
    @SerializedName("isFavorite")
    val isFavorite: Boolean // 실제 필요한 값
)
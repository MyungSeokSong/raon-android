package com.example.raon.features.item.data.remote.dto.list

import com.google.gson.annotations.SerializedName

// 전체 응답 구조
data class ItemListResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ItemDataDto
)

// 페이징 및 아이템 목록 데이터
data class ItemDataDto(
    @SerializedName("products") val items: List<ItemDto>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int
)

// 개별 아이템 상세 정보 (수정된 부분)
data class ItemDto(
    @SerializedName("productId") val itemId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Int,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("seller") val seller: SellerDto,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("createdAt") val createdAt: String,

    // 👇 아래 4개 필드가 추가되었습니다.
    @SerializedName("viewCount") val viewCount: Int,
    @SerializedName("condition") val condition: String,
    @SerializedName("tradeType") val tradeType: String,
    @SerializedName("status") val status: String
)

// 위치 정보
data class LocationDto(
    @SerializedName("locationId") val locationId: Int,
    @SerializedName("address") val address: String
)

// 판매자 정보
data class SellerDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?
)
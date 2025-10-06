package com.example.raon.features.item.data.remote.dto.list

import com.google.gson.annotations.SerializedName

// 전체 응답 구조
data class ItemListResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ItemDataDto // 이름 변경
)

// 페이징 및 아이템 목록 데이터
data class ItemDataDto( // 이름 변경
    @SerializedName("products") val items: List<ItemDto>, // 이름 변경
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int
)

// 개별 아이템 상세 정보
data class ItemDto( // 이름 변경
    @SerializedName("productId") val itemId: Int, // 이름 변경
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Int,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("seller") val seller: SellerDto,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("createdAt") val createdAt: String
)

// 위치 정보 (이하 클래스는 변경 없음)
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
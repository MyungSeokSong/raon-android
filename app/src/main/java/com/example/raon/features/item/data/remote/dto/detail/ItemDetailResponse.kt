package com.example.raon.features.item.data.remote.dto.detail


import com.google.gson.annotations.SerializedName


// 서버 응답의 최상위 구조
// { "code": "OK", "message": "...", "data": { ... } }
data class ItemDetailResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ItemDetailData
)

// 실제 상품 데이터 구조 ("data" 객체 내부)

data class ItemDetailData(
    @SerializedName("productId") val productId: Int,
    @SerializedName("seller") val seller: Seller,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("location") val location: Location,
    @SerializedName("imageUrls") val imageUrls: List<String>,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Int,
    @SerializedName("viewCount") val viewCount: Int,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("condition") val condition: String,
    @SerializedName("tradeType") val tradeType: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
)

data class Seller(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String? // null일 수 있으므로 Nullable
)

data class Category(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("name") val name: String
)

data class Location(
    @SerializedName("locationId") val locationId: Int,
    @SerializedName("address") val address: String
)
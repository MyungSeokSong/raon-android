package com.example.raon.features.profile.data.dto


import com.google.gson.annotations.SerializedName

// API 응답 전체를 감싸는 데이터 클래스
data class MyProductsResponseDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: MyProductsDataDto
)

// data 객체에 해당하는 클래스
data class MyProductsDataDto(
    @SerializedName("products")
    val products: List<MyProductDto>,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("totalElements")
    val totalElements: Int
)

// 개별 상품에 대한 데이터 클래스
// CommonItemUiModel과 필드를 맞추는 것이 중요합니다.
data class MyProductDto(
    @SerializedName("productId")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("location")
    val location: LocationDto,
    @SerializedName("thumbnail")
    val imageUrl: String,
    @SerializedName("status") // "판매중", "거래완료" 등을 구분할 필드
    val status: String,
    @SerializedName("viewCount")
    val viewCount: Int,
    @SerializedName("likeCount")
    val likeCount: Int,
    @SerializedName("chatCount")
    val chatCount: Int,
    @SerializedName("createdAt") // "timeAgo"로 변환될 필드
    val createdAt: String
)


// "location" 객체를 위한 데이터 클래스
data class LocationDto(
    @SerializedName("address") val address: String
    // locationId는 현재 필요 없으므로 생략 가능
)
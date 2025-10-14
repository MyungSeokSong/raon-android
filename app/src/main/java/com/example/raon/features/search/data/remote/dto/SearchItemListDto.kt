package com.example.raon.features.search.data.remote.dto


import com.example.raon.features.search.ui.model.SearchItemUiModel
import com.google.gson.annotations.SerializedName

// 전체 응답 구조
data class SearchItemListDto(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SearchItemDataDto
)

// 페이징 및 아이템 목록 데이터
data class SearchItemDataDto(
    @SerializedName("products") val items: List<SearchItemDto>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int
)

// 개별 아이템 상세 정보 (수정된 부분)
data class SearchItemDto(
    @SerializedName("productId") val itemId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Int,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("location") val location: SearchLocationDto,
    @SerializedName("seller") val seller: SearchSellerDto,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("createdAt") val createdAt: String,

    // 👇 아래 4개 필드가 추가되었습니다.
    @SerializedName("viewCount") val viewCount: Int,
    @SerializedName("condition") val condition: String,
    @SerializedName("tradeType") val tradeType: String,
    @SerializedName("status") val status: String
)

// 위치 정보
data class SearchLocationDto(
    @SerializedName("locationId") val locationId: Int,
    @SerializedName("address") val address: String
)

// 판매자 정보
data class SearchSellerDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?
)

/**
 * SearchItemDto (Data Layer) 객체를 Product (Domain Layer) 객체로 변환합니다.
 * UI에서 필요한 데이터만 추출하여 깔끔한 모델을 만듭니다.
 */
fun SearchItemDto.toDomain(): SearchItemUiModel {
    return SearchItemUiModel(
        id = this.itemId, // Long 타입으로 변환
        title = this.title,
        location = this.location.address,
        price = this.price,
        imageUrl = this.thumbnail.toString(),
        likes = this.favoriteCount,
        timeAgo = this.createdAt,
        viewCount = this.viewCount,
        comments = 0
    )
}
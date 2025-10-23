package com.example.raon.features.chat.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// 채팅방 상세 정보 DTO
/**
 * GET /api/v1/chats/{chatId} API의 전체 응답 구조
 */
@JsonClass(generateAdapter = true)
data class ChatRoomDetailResponse(
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: ChatRoomDetailDataDto? // data 필드는 없을 수도 있으므로 Nullable
)

/**
 * 응답의 "data" 필드 내부 구조
 */
@JsonClass(generateAdapter = true)
data class ChatRoomDetailDataDto(
    @Json(name = "chatId") val chatId: Long,
    @Json(name = "product") val product: ProductInChatDetailDto, // 상품 정보
    @Json(name = "buyer") val buyer: UserInChatDetailDto,     // 구매자 정보
    @Json(name = "seller") val seller: UserInChatDetailDto    // 판매자 정보
)

/**
 * 채팅방 상세 정보 내의 상품 정보 ("product" 필드)
 */
@JsonClass(generateAdapter = true)
data class ProductInChatDetailDto(
    @Json(name = "productId") val productId: Long,
    @Json(name = "location") val location: LocationInChatDetailDto?, // location 정보는 없을 수도 있음
    @Json(name = "thumbnail") val thumbnail: String?, // 썸네일 URL (S3 키)
    @Json(name = "title") val title: String?,
    @Json(name = "price") val price: Int?,
    @Json(name = "status") val status: String?, // 예: "AVAILABLE", "RESERVED", "SOLD"
    @Json(name = "isDeleted") val isDeleted: Boolean?
)

/**
 * 상품 정보 내의 위치 정보 ("location" 필드)
 */
@JsonClass(generateAdapter = true)
data class LocationInChatDetailDto(
    @Json(name = "locationId") val locationId: Int?,
    @Json(name = "address") val address: String?
)

/**
 * 채팅방 상세 정보 내의 사용자 정보 ("buyer", "seller" 필드)
 */
@JsonClass(generateAdapter = true)
data class UserInChatDetailDto(
    @Json(name = "userId") val userId: Int,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "profileImage") val profileImage: String?, // 프로필 이미지 URL (S3 키)
    @Json(name = "isDeleted") val isDeleted: Boolean?
)

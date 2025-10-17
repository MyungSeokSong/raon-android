package com.example.raon.features.item.ui.detail.model


// UI에서 사용할 최종 데이터 클래스
data class ItemDetailModel(
    val id: Int,
    val imageUrls: List<String>,
    val sellerNickname: String,
    val sellerProfileUrl: String?,
    val sellerAddress: String,
    val title: String,
    val condition: String,
    val category: String, // 카테고리는 하나의 문자열로 합쳐서 사용
    val createdAt: String, // 시간 표시는 나중에 변환
    val description: String,
    val favoriteCount: Int,
    val viewCount: Int,
    val price: Int,
    val isFavorite: Boolean, // 내 관심 목록 인지 판단
    val sellerId: Int,   // item 판매자의 userId
    val isMine: Boolean // 내 상품 여부 확인


)
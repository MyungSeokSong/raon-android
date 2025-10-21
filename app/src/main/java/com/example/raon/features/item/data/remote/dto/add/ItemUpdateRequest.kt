package com.example.raon.features.item.data.remote.dto.add

import com.google.gson.annotations.SerializedName

// s3에 저장, 삭제 때문에 add와 별도의 Dto를 만들어 사용함
data class ItemUpdateRequest(
    @SerializedName("categoryId")
    val categoryId: Int?,
    // 👇 이 필드를 추가해주세요.
    @SerializedName("locationId")
    val locationId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("condition")
    val condition: String,
    @SerializedName("tradeType")
    val tradeType: String,
    // TODO: 이미지 수정 로직에 따라 이 필드는 변경될 수 있습니다.
    // (예: newImageUrls, deletedImageUrls 등으로 분리)
    @SerializedName("imageUrls")
    val imageUrls: List<String>
)
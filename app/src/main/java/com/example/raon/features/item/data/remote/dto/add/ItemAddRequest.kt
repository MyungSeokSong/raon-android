package com.example.raon.features.item.data.remote.dto.add

import com.google.gson.annotations.SerializedName

data class ItemAddRequest(
    @SerializedName("categoryId")   // 실제 JSON 필드 이름과 매칭시킴
    val categoryId: Int?,
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
    @SerializedName("imageUrls")
    val imageList: List<String>

)

package com.example.raon.features.item.z_data.remote.dto

import com.google.gson.annotations.SerializedName


data class ItemResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ItemData?
)

data class ItemData(
    @SerializedName("productId")
    val productId: Int
)

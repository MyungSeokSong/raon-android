package com.example.raon.features.category.data.remote.dto

import com.google.gson.annotations.SerializedName

// 최상위 JSON 응답 전체를 감싸는 클래스
data class CategoryResponseDto(
    // JSON의 "data" 키에 해당하는 부분을 매핑
    @SerializedName("data")
    val data: CategoryDataDto
)

// JSON의 "data" 객체 내부에 있는 구조를 나타내는 클래스
data class CategoryDataDto(
    // JSON의 "categories" 키에 해당하는 부분을 매핑
    @SerializedName("categories")
    val categories: List<CategoryDto>
)

// "categories" 배열 안의 각 카테고리 객체
data class CategoryDto(
    @SerializedName("categoryId")
    val categoryId: Long,

    @SerializedName("level")
    val level: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("isLeaf")
    val isLeaf: Boolean,

    // 자식 카테고리 리스트 (없을 수도 있으므로 nullable)
    @SerializedName("children")
    val children: List<CategoryDto>?
)
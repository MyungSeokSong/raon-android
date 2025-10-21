package com.example.raon.features.item.ui.add

import android.net.Uri

//import com.example.raon.features.item.domain.model.Category // Category 모델 임포트 필요

data class AddItemUiState(
    val locationId: Int = 1,
    val title: String = "",
    val description: String = "",
    val price: String = "", // ◀◀ Int에서 String으로 변경
    val seletedImages: List<Uri> = emptyList(), // 새로 추가하는 로컬 이미지 (Uri)

    // 수정 시 보여줄 기존 이미지 (URL)
    val existingImageUrls: List<String> = emptyList(),
    // 삭제된 기존 이미지 URL을 추적하기 위한 리스트 추가
    val deletedImageUrls: List<String> = emptyList(),

    // 선택한 카테고리 데이터를 담는 변수
    val selectedCategoryId: Int? = -1,     // 선택된 카테고리의 고유 ID
    val selectedCategoryName: String? = "카테고리 선택", // 선택된 카테고리의 이름


    // val condition: String? = "USED", // ❌ 이 코드를 아래 코드로 변경
    val productCondition: ProductCondition = ProductCondition.USED, // ✅ 이렇게 변경 (기본값 '중고')


    val isLoading: Boolean = false,
    val isSuccess: Boolean = false, // 등록 성공 여부 상태
    val selectedCategory: Category? = null, // ◀◀ 선택된 카테고리 저장을 위해 추가


    val isCategoryValid: Boolean = false, // 👈 가격 유효성 상태 추가 (기본값 false)
    val isPriceValid: Boolean = false, // 👈 가격 유효성 상태 추가 (기본값 false)

    // 👇 이 줄을 추가해주세요.
    val errorMessage: String? = null


)


// 1. 상품 상태를 나타내는 enum 클래스 정의
enum class ProductCondition(val displayName: String) {
    USED("중고 상품"),
    NEW("새 상품")
}


// 만약 Category 모델이 없다면 아래와 같이 만들어주세요.
// 경로: features/item/domain/model/Category.kt
data class Category(
    val id: String,
    val name: String
)
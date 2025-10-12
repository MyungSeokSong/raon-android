package com.example.raon.features.category.domain

/**
 * UI 계층에서 사용할 최종 카테고리 데이터 모델입니다.
 *
 * @param id 카테고리의 고유 ID
 * @param name 카테고리 이름 (예: "여성의류", "패딩")
 * @param hasChildren 하위 카테고리의 존재 여부 (`isLeaf`의 반대 개념)
 * @param parentId (Optional) 부모 카테고리의 ID. ViewModel의 임시 데이터 처리를 위해 포함.
 */
//@Parcelize
//data class Category(
//    val id: Long,
//    val name: String,
//    val hasChildren: Boolean,
//    val parentId: Long? = null
//) : Parcelable

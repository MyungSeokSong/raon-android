//package com.example.raon.features.category.data.local
//
//// =================================================================================
//// ▼▼▼ features/category/data/local/CategoryEntity.kt (새 파일) ▼▼▼
//// =================================================================================
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "categories")
//data class CategoryEntity(
//    @PrimaryKey val categoryId: Long,
//    val name: String,
//    val parentId: Long?, // 최상위 카테고리는 parentId가 null입니다.
//    val isLeaf: Boolean
//)
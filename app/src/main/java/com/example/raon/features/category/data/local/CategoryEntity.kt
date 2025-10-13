package com.example.raon.features.category.data.local


// Room DB에 저장할 Entity
import androidx.room.Entity
import androidx.room.PrimaryKey


// 'categories'라는 이름의 테이블을 생성합니다.
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val categoryId: Long,
    val parentId: Long?, // 부모 카테고리의 ID, 최상위 카테고리는 null이 됩니다.
    val level: Int,
    val name: String,
    val isLeaf: Boolean
)
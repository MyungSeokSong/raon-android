package com.example.raon.features.category.data


import com.example.raon.features.category.data.local.CategoryEntity
import com.example.raon.features.category.data.remote.dto.CategoryDto

// DTO 리스트를 Entity 리스트로 변환하는 확장 함수
fun List<CategoryDto>.toEntityList(): List<CategoryEntity> {
    // 재귀 함수를 사용하여 모든 중첩된 카테고리를 평탄화합니다.
    fun flattenCategories(categories: List<CategoryDto>, parentId: Long?): List<CategoryEntity> {
        val entityList = mutableListOf<CategoryEntity>()
        categories.forEach { dto ->
            // 현재 DTO를 Entity로 변환하여 리스트에 추가
            entityList.add(
                CategoryEntity(
                    categoryId = dto.categoryId,
                    parentId = parentId,
                    level = dto.level,
                    name = dto.name,
                    isLeaf = dto.isLeaf
                )
            )
            // 자식 카테고리가 있으면, 재귀적으로 호출하여 리스트에 추가
            dto.children?.let { children ->
                entityList.addAll(flattenCategories(children, dto.categoryId))
            }
        }
        return entityList
    }

    return flattenCategories(this, null) // 최상위 카테고리의 parentId는 null
}
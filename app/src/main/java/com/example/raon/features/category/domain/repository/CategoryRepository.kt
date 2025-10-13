package com.example.raon.features.category.domain.repository

import com.example.raon.features.category.data.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    /**
     * 필요할 경우, 카테고리 데이터를 초기 설정합니다.
     * (DB가 비어있으면 서버에서 데이터를 가져와 저장)
     */
    suspend fun setupCategoriesIfNeeded()


    // 카테고리를 parentId에 따라 조회하는 함수
    fun getCategoriesByParentId(parentId: Int?): Flow<List<CategoryEntity>>

}
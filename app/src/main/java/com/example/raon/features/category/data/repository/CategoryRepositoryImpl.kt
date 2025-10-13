package com.example.raon.features.category.data.repository

import android.util.Log
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.handleApi
import com.example.raon.features.category.data.local.CategoryDao
import com.example.raon.features.category.data.local.CategoryEntity
import com.example.raon.features.category.data.remote.api.CategoryApiService
import com.example.raon.features.category.data.toEntityList // Mapper 함수 import
import com.example.raon.features.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject // Hilt/Dagger 사용 시

// 생성자를 통해 외부로부터 ApiService와 Dao를 주입받습니다.
class CategoryRepositoryImpl @Inject constructor(
    private val apiService: CategoryApiService,
    private val categoryDao: CategoryDao
) : CategoryRepository { // CategoryRepository 인터페이스를 구현(implements)

    // 인터페이스에 정의된 함수를 실제로 구현하는 부분
    override suspend fun setupCategoriesIfNeeded() {
        val isDbEmpty = categoryDao.getCategoryCount() == 0

        if (isDbEmpty) {
            // 1. '배송 대행사(handleApi)'에 API 호출을 맡기고 '결과 보고서(ApiResult)'를 받음
            val apiResult = handleApi { apiService.getAllCategories() }

            // 2. '결과 보고서'의 내용을 확인하고 그에 맞게 행동함
            when (apiResult) {
                is ApiResult.Success -> {
                    // ✨ 성공 로그 추가
                    Log.d("CatetoryRepository", "API Success! Data received: ${apiResult.data}")

                    // 성공! 받은 데이터를 DB에 저장
                    val categoryEntities = apiResult.data.data.categories.toEntityList()
                    categoryDao.insertAll(categoryEntities)

                    // ✨ DB 저장 후 로그 추가
                    Log.d(
                        "CatetoryRepository",
                        "${categoryEntities.size} categories have been saved to the database."
                    )
                }

                is ApiResult.Error -> {
                    // 서버 에러! 로그 남기기
                    Log.e("CategoryRepo", "API Error: ${apiResult.code}")
                }

                is ApiResult.Exception -> {
                    // 네트워크 예외! 로그 남기기
                    Log.e("CategoryRepo", "Network Exception: ${apiResult.e.message}")
                }
            }
        }
    }

    // ✨ 구현 내용을 변경합니다.
    override fun getCategoriesByParentId(parentId: Int?): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByParentId(parentId)
    }
}
package com.example.raon.features.search.data.repository


import android.util.Log
import com.example.raon.features.search.data.remote.api.SearchApiService
import com.example.raon.features.search.data.remote.dto.toDomain
import com.example.raon.features.search.domain.repository.SearchRepository
import com.example.raon.features.search.ui.model.SearchItemUiModel
import javax.inject.Inject

/**
 * SearchRepository의 실제 구현체입니다.
 * 원격 데이터 소스(API)로부터 데이터를 가져오는 역할을 담당합니다.
 * Hilt나 Dagger 같은 DI 라이브러리를 통해 SearchApiService를 주입받습니다.
 */
class SearchRepositoryImpl @Inject constructor(
    private val apiService: SearchApiService
) : SearchRepository {

    /**
     * ViewModel에서 받은 모든 검색 조건을 사용하여
     * SearchApiService를 호출하고, 그 결과를 Domain Model로 변환하여 반환합니다.
     */
    override suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: String?,
        categoryId: Int?,
        locationId: Int?,
        minPrice: Int?,
        maxPrice: Int?,
        condition: String?,
        tradeType: String?,
        status: String?
    ): Result<List<SearchItemUiModel>> {
        // 네트워크 요청은 실패할 수 있으므로 try-catch로 감싸줍니다.
        return try {
            // ViewModel에서 받은 모든 파라미터를 그대로 API 서비스에 전달합니다.
            // Retrofit이 null인 파라미터는 자동으로 요청 URL에서 제외시켜 줍니다.
            val responseDto = apiService.searchProducts(
                keyword = keyword,
                page = page,
                size = size,
                sort = sort,
                categoryId = categoryId,
                locationId = locationId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                condition = condition,
                tradeType = tradeType,
                status = status
            )

            // 서버 응답(DTO)을 UI에서 사용할 데이터(Domain Model)로 변환합니다.
            val domainModelList = responseDto.data.items.map { it.toDomain() }

            // 👇 성공 로그 추가
            Log.d("SearchRepository", "✅ 데이터 수신 성공: ${domainModelList.size}개 아이템 수신")
            Log.d("SearchRepository", "✅ 내용: $domainModelList")

            // 성공 결과를 Result 객체로 감싸서 반환합니다.
            Result.success(domainModelList)

        } catch (e: Exception) {
            // 네트워크 오류, 서버 응답 오류 등 모든 예외를 여기서 잡습니다.
            // 발생한 예외를 실패 결과(Result.failure)로 감싸서 반환합니다.
            Result.failure(e)
        }
    }
}
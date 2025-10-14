package com.example.raon.features.search.data.remote.api


//import com.example.raon.features.search.data.remote.dto.SearchProductResponseDto
import com.example.raon.features.search.data.remote.dto.SearchItemListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("api/v1/products")
    suspend fun searchProducts(
        // 필수 파라미터
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 20,

        // --- 선택적 필터 및 정렬 파라미터 (Nullable로 선언) ---

        // 정렬
        @Query("sort") sort: String?, // 예: "createdAt,desc" (최신순), "price,asc" (낮은 가격순)

        // 필터
        @Query("categoryId") categoryId: Int?,
        @Query("locationId") locationId: Int?,
        @Query("minPrice") minPrice: Int?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("condition") condition: String?, // 예: "USED", "NEW"
        @Query("tradeType") tradeType: String?, // 예: "DIRECT", "DELIVERY"
        @Query("status") status: String? // 예: "AVAILABLE" (판매중)
    ): SearchItemListDto
}
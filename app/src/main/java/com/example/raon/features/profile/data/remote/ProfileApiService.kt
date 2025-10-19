package com.example.raon.features.profile.data.remote


import com.example.raon.features.profile.data.dto.MyProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApiService {

    /**
     * 나의 판매 상품 목록을 가져오는 API
     * @param page 페이지 번호 (0부터 시작)
     */
    @GET("/api/v1/me/products")
    suspend fun getMyProducts(
        @Query("page") page: Int = 0
    ): MyProductsResponseDto
}
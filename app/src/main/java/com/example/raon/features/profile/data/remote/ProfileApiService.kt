package com.example.raon.features.profile.data.remote


import com.example.raon.features.profile.data.dto.MyProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApiService {

    // 나의 판매 상품 가져오기 API
    @GET("/api/v1/me/products")
    suspend fun getMyProducts(
        @Query("page") page: Int = 0
    ): MyProductsResponseDto

    // 찜 목록 가져오기 API
    @GET("api/v1/me/favorites")
    suspend fun getFavorites(@Query("page") page: Int = 0): MyProductsResponseDto
}
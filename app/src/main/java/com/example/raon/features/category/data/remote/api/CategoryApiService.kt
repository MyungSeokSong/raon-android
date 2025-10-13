package com.example.raon.features.category.data.remote.api

import com.example.raon.features.category.data.remote.dto.CategoryResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApiService {

    @GET("api/v1/categories")
    suspend fun getAllCategories(): Response<CategoryResponseDto>


}
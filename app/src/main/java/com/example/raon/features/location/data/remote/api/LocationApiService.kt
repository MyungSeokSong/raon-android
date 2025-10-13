package com.example.raon.features.location.data.remote.api

import com.example.raon.core.network.dto.ApiResponse
import com.example.raon.features.location.data.remote.dto.LocationResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {

    @GET("api/v1/locations")
    suspend fun getLocations(
        // "@Query("key")" : URL에 붙을 key 이름
        // "value: Type" : 함수가 받을 파라미터
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<ApiResponse<LocationResponseDto>>


}
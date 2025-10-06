package com.example.raon.features.user.data.remote

import com.example.raon.features.user.data.dto.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {
    @GET("api/v1/me")
    suspend fun getProfile(): Response<ProfileResponse>
}
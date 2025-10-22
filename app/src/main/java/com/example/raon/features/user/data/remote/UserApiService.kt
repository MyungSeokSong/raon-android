package com.example.raon.features.user.data.remote

import com.example.raon.features.user.data.dto.ProfileResponse
import com.example.raon.features.user.data.dto.SimpleApiResponse
import com.example.raon.features.user.data.dto.UpdateNicknameRequest
import com.example.raon.features.user.data.dto.UpdateProfileImageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {
    @GET("api/v1/me")
    suspend fun getProfile(): Response<ProfileResponse>


    // [추가] 닉네임 변경 API (Postman 1번 이미지)
    @PATCH("api/v1/me/nickname")
    suspend fun updateNickname(
        @Body request: UpdateNicknameRequest
    ): Response<SimpleApiResponse>

    // [추가] 프로필 이미지 URL 변경 API (Postman 2번 이미지)
    @PATCH("api/v1/me/profile-image")
    suspend fun updateProfileImage(
        @Body request: UpdateProfileImageRequest
    ): Response<SimpleApiResponse>
}
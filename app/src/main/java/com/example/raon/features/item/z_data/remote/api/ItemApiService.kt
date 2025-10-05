package com.example.raon.features.item.z_data.remote.api

import com.example.raon.features.item.z_data.remote.dto.ItemRequest
import com.example.raon.features.item.z_data.remote.dto.ItemResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


// 2단계 통신을 위한 인터페이스
interface ItemApiService {
    /** 1. 이미지들을 업로드하고 URL 목록을 받아오는 API (가정) */
    @Multipart
    @POST("api/v1/images") // 실제 이미지 업로드 API 엔드포인트
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>
    ): List<String> // 예시: 이미지 URL 문자열 리스트를 반환한다고 가정

    /** 2. 최종 아이템 정보를 JSON으로 등록하는 API */
    @POST("api/v1/product")
    suspend fun postItem(
        @Body itemData: ItemRequest
    ): ItemResponse
}
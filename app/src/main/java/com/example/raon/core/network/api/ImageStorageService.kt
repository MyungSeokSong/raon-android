package com.example.raon.core.network.api

import com.example.raon.core.network.dto.PresignedUrlRequest
import com.example.raon.core.network.dto.PresignedUrlResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface ImageStorageService {

    // 서버에 Presigned URL 요청
    @POST("default/raon-presigned-url-generator") // API Gateway의 세부 경로
    suspend fun getPresignedUrl(
        @Body request: PresignedUrlRequest
    ): PresignedUrlResponse
    

    // S3로 실제 이미지 파일 업로드
    // @Url 어노테이션은 baseUrl을 무시하고 파라미터로 받은 url 주소로 직접 요청을 보냄
    @Headers("No-Authentication: true")  // 이 요청에는 Authorization 헤더를 추가하지 않도록 하기
    @PUT
    suspend fun uploadImage(
        @Url url: String,
        @Body file: RequestBody
    ): retrofit2.Response<Unit>
}
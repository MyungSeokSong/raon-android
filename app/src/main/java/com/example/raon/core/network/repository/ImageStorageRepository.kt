package com.example.raon.core.network.repository

import com.example.raon.core.network.api.ImageStorageService
import com.example.raon.core.network.dto.PresignedUrlRequest
import okhttp3.RequestBody
import javax.inject.Inject

// 이미지 CURD 관련 Repository
class ImageStorageRepository @Inject constructor(
    private val imageStorageService: ImageStorageService
) {

    // S3 이미지 조회 PresignedUrl
    suspend fun getPresignedImageUrl(imageKey: String): Result<String> {
        return try {
            val response = imageStorageService.getPresignedImageUrl(imageKey)
            Result.success(response.url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // S3 이미지 업로드 PresignedUrl

    suspend fun getPresignedUrl(uploadType: String, fileName: String): Result<String> {
        return try {
            val request = PresignedUrlRequest(uploadType, fileName)
            val response = imageStorageService.getPresignedUrl(request)
            Result.success(response.url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFile(url: String, file: RequestBody): Result<Unit> {
        return try {
            val response = imageStorageService.uploadImage(url, file)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("S3 Upload Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
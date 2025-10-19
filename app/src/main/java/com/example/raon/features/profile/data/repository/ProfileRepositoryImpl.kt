package com.example.raon.features.profile.data.repository

import android.util.Log
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.core.ui.model.ItemListUiModel
import com.example.raon.features.profile.data.dto.MyProductDto
import com.example.raon.features.profile.data.remote.ProfileApiService
import com.example.raon.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

private val S3_BASE_URL = "https://raon-market-images-prod.s3.ap-northeast-2.amazonaws.com/"

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val imageStorageRepository: ImageStorageRepository
) : ProfileRepository {

    // 내 판매상품 가져오기
    override suspend fun getMyProducts(): Result<List<ItemListUiModel>> {
        return try {
            val response = profileApiService.getMyProducts()
            // ❗️ 판매 내역이므로 isFavoriteList를 false로 전달
            val uiModels =
                processProductsAndGetPresignedUrls(response.data.products, isFavoriteList = false)
            Result.success(uiModels)
        } catch (e: Exception) {
            Log.e("DEBUG_RAON", "[Repo] GetMyProducts 실패: ${e.message}")
            Result.failure(e)
        }
    }

    // 찜 목록 가져오기
    override suspend fun getFavorites(): Result<List<ItemListUiModel>> {
        return try {
            val response = profileApiService.getFavorites()
            // ❗️ 찜 목록이므로 isFavoriteList를 true로 전달
            val uiModels =
                processProductsAndGetPresignedUrls(response.data.products, isFavoriteList = true)
            Result.success(uiModels)
        } catch (e: Exception) {
            Log.e("DEBUG_RAON", "[Repo] GetFavorites 실패: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 상품 DTO 리스트를 받아 Presigned URL 처리 후 최종 UI 모델 리스트로 변환하는 공통 함수
     */
    private suspend fun processProductsAndGetPresignedUrls(
        products: List<MyProductDto>,
        isFavoriteList: Boolean // ❗️ 파라미터 추가
    ): List<ItemListUiModel> {
        return coroutineScope {
            val urlJobs: List<Deferred<String?>> = products.map { item ->
                async {
                    item.imageUrl?.let { key ->
                        val objectKey = key.removePrefix(S3_BASE_URL)
                        imageStorageRepository.getPresignedImageUrl(objectKey).getOrNull()
                    }
                }
            }

            val presignedUrls = urlJobs.awaitAll()

            products.zip(presignedUrls).map { (dto, newUrl) ->
                ItemListUiModel(
                    id = dto.id,
                    imageUrl = newUrl ?: dto.imageUrl,
                    title = dto.title,
                    location = dto.location.address,
                    timeAgo = dto.createdAt,
                    price = dto.price,
                    comments = dto.chatCount,
                    likes = dto.likeCount,
                    viewCount = dto.viewCount,
                    status = dto.status,
                    // ❗️ 파라미터로 받은 값으로 isFavorite 상태 설정
                    isFavorite = isFavoriteList
                )
            }
        }
    }
}
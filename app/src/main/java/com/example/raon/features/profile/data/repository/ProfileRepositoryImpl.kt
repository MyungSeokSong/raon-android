package com.example.raon.features.profile.data.repository


import android.util.Log
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.core.ui.model.ItemListUiModel
import com.example.raon.features.profile.data.remote.ProfileApiService
import com.example.raon.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

//
private val S3_BASE_URL = "https://raon-market-images-prod.s3.ap-northeast-2.amazonaws.com/"


// Data Layer: 인터페이스 구현
class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val imageStorageRepository: ImageStorageRepository  // 이미지 URL을 받아올 repository
) : ProfileRepository {

    override suspend fun getMyProducts(): Result<List<ItemListUiModel>> {

        return try {
            // 1. 서버로 부터 데이터 가져오기
            val responseDto = profileApiService.getMyProducts()
            val originalProducts = responseDto.data.products


            // 2. 각 아이템의 thumbnail(객체 키)로 Presigned URL을 병렬로 요청
            val itemsWithPresignedUrl = coroutineScope {
                val urlJobs = originalProducts.map { item ->
                    async {
                        item.imageUrl?.let { key ->
                            // S3_BASE_URL을 지우고 순수 이미지 이름만 파싱
                            val objectKey = key.removePrefix(S3_BASE_URL)
                            // 파싱한 이미지 이름으로 presigned URL 요청
                            imageStorageRepository.getPresignedImageUrl(objectKey).getOrNull()
                        }
                    }
                }

                // 모든 URL 요청이 끝날 때까지 기다림
                val presignedUrls = urlJobs.awaitAll()
                originalProducts.zip(presignedUrls)
                    .map { (dto, newUrl) -> // 👇 3. 묶은 데이터를 기반으로 최종 UI 모델 생성
                        ItemListUiModel(
                            id = dto.id,
                            // 새로 받아온 newUrl을 사용하고, 실패 시 기존 imageUrl 사용, 그마저도 없으면 빈 문자열
                            imageUrl = newUrl ?: dto.imageUrl ?: "",
                            title = dto.title,
                            location = dto.location.address,
                            timeAgo = dto.createdAt, // TODO: 시간 형식 변환
                            price = dto.price,
                            comments = dto.chatCount,
                            likes = dto.likeCount,
                            viewCount = dto.viewCount,
                            status = dto.status
                        )
                    }
            }


            // API에서 받은 DTO 리스트를 UI 모델 리스트로 변환 (mapping)
//            val uiModels = response.data.products.map { dto ->
//                ItemListUiModel(
//                    id = dto.id,
//                    imageUrl = dto.imageUrl ?: "",
//                    title = dto.title,
//                    location = dto.location.address,
//                    timeAgo = dto.createdAt, // TODO: "15분 전"과 같은 형식으로 변환 필요
//                    price = dto.price,
//                    comments = dto.chatCount,
//                    likes = dto.likeCount,
//                    viewCount = dto.viewCount,
//                    status = dto.status
//                )
//            }

            // 로그
            Log.d("DEBUG_RAON", "[Repository] API 호출 성공! 변환된 아이템 개수: ${itemsWithPresignedUrl.size}")
            Log.d("DEBUG_RAON", "[Repository] 첫 번째 아이템: ${itemsWithPresignedUrl.firstOrNull()}")

            Log.d(
                "DEBUG_RAON",
                "[Repository] 첫 번째 아이템: ${itemsWithPresignedUrl.firstOrNull()?.imageUrl}"
            )


            Result.success(itemsWithPresignedUrl)
        } catch (e: Exception) {

            Log.e("DEBUG_RAON", "[Repository] API 호출 실패: ${e.message}")
            Result.failure(e)
        }


    }
}
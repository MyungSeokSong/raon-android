package com.example.raon.features.item.data.repository

import android.content.Context
import android.net.Uri
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.features.item.data.remote.api.ItemApiService
import com.example.raon.features.item.data.remote.dto.add.ItemAddRequest
import com.example.raon.features.item.data.remote.dto.add.ItemResponse
import com.example.raon.features.item.data.remote.dto.list.ItemDto
import com.example.raon.features.item.ui.list.model.ItemUiModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemApiService: ItemApiService,
    private val storageRepository: ImageStorageRepository,
    @ApplicationContext private val context: Context
) : ItemRepository {

    override suspend fun getItems(page: Int): List<ItemDto> {
        val response = itemApiService.getItems(page = page)
        if (response.isSuccessful) {
            return response.body()?.data?.items ?: emptyList()
        } else {
            throw Exception("API 호출 실패: ${response.code()}")
        }
    }

    // --- 아래 함수가 새로 추가/구현되었습니다 ---
    override suspend fun getItemsWithViewableUrls(page: Int): List<ItemUiModel> {
        // 1. 메인 서버에서 아이템 목록(DTO)을 가져옵니다.
        val itemsDto = getItems(page)

        // 2. 각 아이템의 thumbnail(객체 키)로 Presigned URL을 병렬로 요청합니다.
        val itemsWithPresignedUrl = coroutineScope {
            val urlJobs = itemsDto.map { item ->
                async {
                    item.thumbnail?.let { key ->
                        storageRepository.getPresignedImageUrl(key).getOrNull()
                    }
                }
            }
            val presignedUrls = urlJobs.awaitAll()
            itemsDto.zip(presignedUrls) // (ItemDto, PresignedUrl) 쌍으로 묶기
        }

        // 3. 최종 UI 모델로 변환하여 반환합니다.
        return itemsWithPresignedUrl.map { (itemDto, presignedUrl) ->
            itemDto.toUiModel(presignedUrl)
        }
    }

    override suspend fun postNewItem(
        title: String,
        description: String,
        price: Int,
        imageUris: List<Uri>
    ): ItemResponse {
        try {
            val imageUrls = uploadImagesAndGetS3Urls(imageUris)
            if (imageUris.isNotEmpty() && imageUrls.isEmpty()) {
                return ItemResponse(
                    code = "IMAGE_UPLOAD_FAILED",
                    message = "이미지 업로드 실패",
                    data = null
                )
            }

            val itemRequest = ItemAddRequest(
                categoryId = 270,
                locationId = 435,
                title = title,
                description = description,
                price = price,
                condition = "USED",
                tradeType = "DIRECT",
                imageList = imageUrls
            )

            return itemApiService.postItem(itemRequest)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            return try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                ItemResponse(
                    code = errorResponse.code,
                    message = errorResponse.message,
                    data = null
                )
            } catch (jsonE: Exception) {
                ItemResponse(code = "PARSING_ERROR", message = "에러 응답 파싱 실패", data = null)
            }
        } catch (e: Exception) {
            return ItemResponse(
                code = "UNKNOWN_ERROR",
                message = e.message ?: "알 수 없는 오류 발생",
                data = null
            )
        }
    }

    private suspend fun uploadImagesAndGetS3Urls(imageUris: List<Uri>): List<String> =
        coroutineScope {
            if (imageUris.isEmpty()) return@coroutineScope emptyList()

            val uploadJobs = imageUris.map { uri ->
                async {
                    try {
                        val fileName =
                            "item-image-${System.currentTimeMillis()}-${uri.lastPathSegment}"
                        val presignedUrl =
                            storageRepository.getPresignedUrl("item", fileName).getOrNull()
                        presignedUrl?.let {
                            val requestBody = context.contentResolver.openInputStream(uri)?.use {
                                it.readBytes().toRequestBody(
                                    context.contentResolver.getType(uri)?.toMediaTypeOrNull()
                                )
                            } ?: return@async null

                            val uploadResult = storageRepository.uploadFile(it, requestBody)
                            if (uploadResult.isSuccess) {
                                it.substringBefore("?")
                            } else null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            uploadJobs.awaitAll().filterNotNull()
        }

    // --- 아래 변환 함수들이 추가되었습니다 ---
    private fun ItemDto.toUiModel(presignedUrl: String?): ItemUiModel {
        return ItemUiModel(
            id = this.itemId,
            title = this.title,
            location = this.location.address,
            timeAgo = formatTimeAgo(this.createdAt),
            price = this.price,
            imageUrl = presignedUrl ?: "",
            likes = this.favoriteCount,
            comments = 0
        )
    }

    private fun formatTimeAgo(createdAt: String): String {
        return try {
            val createdTime = OffsetDateTime.parse(createdAt)
            val now = OffsetDateTime.now()

            val minutes = ChronoUnit.MINUTES.between(createdTime, now)
            val hours = ChronoUnit.HOURS.between(createdTime, now)
            val days = ChronoUnit.DAYS.between(createdTime, now)

            when {
                minutes < 1 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                days < 7 -> "${days}일 전"
                else -> "${createdTime.toLocalDate()}"
            }
        } catch (e: Exception) {
            "시간 정보 없음"
        }
    }
}

// API 에러 응답 파싱용 DTO
data class ErrorResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("errors")
    val errors: Map<String, String>?
)
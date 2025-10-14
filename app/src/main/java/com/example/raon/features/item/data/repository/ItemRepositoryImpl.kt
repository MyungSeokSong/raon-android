package com.example.raon.features.item.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.handleApi
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.features.chat.data.remote.dto.CreateChatRoomResponseDto
import com.example.raon.features.chat.data.remote.dto.GetChatRoomResponseDto
import com.example.raon.features.item.data.remote.api.ItemApiService
import com.example.raon.features.item.data.remote.dto.add.ItemAddRequest
import com.example.raon.features.item.data.remote.dto.add.ItemResponse
import com.example.raon.features.item.data.remote.dto.detail.ItemDetailData
import com.example.raon.features.item.data.remote.dto.list.ItemDto
import com.example.raon.features.item.ui.detail.model.ItemDetailModel
import com.example.raon.features.item.ui.list.model.ItemUiModel
import com.example.raon.features.user.domain.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
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
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ItemRepository {

    private val S3_BASE_URL = "https://raon-market-images-prod.s3.ap-northeast-2.amazonaws.com/"


    // itemList 가져오기
    override suspend fun getItems(page: Int): List<ItemDto> {
        val response = itemApiService.getItems(page = page)
        if (response.isSuccessful) {
            return response.body()?.data?.items ?: emptyList()
        } else {
            throw Exception("API 호출 실패: ${response.code()}")
        }
    }

    // 가져온 itemList에서 이미지 키값으로 presignedURL 가져와서 이미지 받아오기
    override suspend fun getItemsWithViewableUrls(page: Int): List<ItemUiModel> {
        // 1. 메인 서버에서 아이템 목록(DTO)을 가져오기
        val itemsDto = getItems(page)

        // 2. 각 아이템의 thumbnail(객체 키)로 Presigned URL을 병렬로 요청
        val itemsWithPresignedUrl = coroutineScope {
            val urlJobs = itemsDto.map { item ->
                async {
                    item.thumbnail?.let { key ->

                        // S3_BASE_URL을 지우고 순수 이미지 이름만 파싱
                        val objectKey = key.removePrefix(S3_BASE_URL)

                        // 파싱한 이미지 이름으로 presigned URL 요청
                        storageRepository.getPresignedImageUrl(objectKey).getOrNull()
                    }
                }
            }
            val presignedUrls = urlJobs.awaitAll()
            itemsDto.zip(presignedUrls) // (ItemDto, PresignedUrl) 쌍으로 묶기
        }

        // 3. 최종 UI 모델로 변환하여 반환
        return itemsWithPresignedUrl.map { (itemDto, presignedUrl) ->
            itemDto.toUiModel(presignedUrl)
        }
    }

    // 새 item 게시글 등록
    override suspend fun postNewItem(
        title: String,
        description: String,
        price: Int,
        imageUris: List<Uri>,
        categoryId: Int?,
        condition: String
    ): ItemResponse {
        try {
            // ✨✨✨ 핵심 부분 ✨✨✨
            // 1. userRepository에서 Flow를 가져온 뒤 .first()를 호출해 최신 User 데이터를 꺼냅니다.
            //    이 작업은 비동기이므로 suspend 함수 내에서만 가능합니다.
            val currentUser = userRepository.getUserProfile().first()

            // 2. 데이터가 null일 수 있으므로 안전하게 처리합니다.
            //    사용자 정보가 있으면 그 사용자의 locationId를, 없으면 기본값(예: 435)을 사용합니다.
            val userLocationId = currentUser?.locationId ?: 1 // 예시: User 모델에 locationId가 있다고 가정


            Log.d("imageUpload", "imageUris : ${imageUris.size}")

            // presigned url 경로
            val imageUrls = uploadImagesAndGetS3Urls(imageUris)

            Log.d("imageUpload", "imageUrls : ${imageUrls}")


            // 앱에서 선택한 이미지 존재 && presigned url 경로 없을 때
            // -> 이미지를 업로드 하려다가 서버에서 실패한 상황
            if (imageUris.isNotEmpty() && imageUrls.isEmpty()) {
                return ItemResponse(
                    code = "IMAGE_UPLOAD_FAILED",
                    message = "이미지 업로드 실패",
                    data = null
                )
            }

            val itemRequest = ItemAddRequest(
                categoryId = categoryId,
                locationId = userLocationId,
                title = title,
                description = description,
                price = price,
                condition = condition,
                tradeType = "DIRECT",
                imageList = imageUrls
            )

            Log.d("imageUpload", "서버 저장 이미지 url : ${imageUrls}")


            return itemApiService.postItem(itemRequest)

        } catch (e: HttpException) {
            Log.d("imageUpload", "HttpException : ${e.code()}")

            val errorBody = e.response()?.errorBody()?.string()
            return try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                ItemResponse(
                    code = errorResponse.code,
                    message = errorResponse.message,
                    data = null
                )
            } catch (jsonE: Exception) {
                Log.d("imageUpload", "jsonE : ${jsonE.message}")

                ItemResponse(code = "PARSING_ERROR", message = "에러 응답 파싱 실패", data = null)
            }
        } catch (e: Exception) {
            Log.d("imageUpload", "Exception : ${e.message}")

            return ItemResponse(
                code = "UNKNOWN_ERROR",
                message = e.message ?: "알 수 없는 오류 발생",
                data = null
            )
        }
    }


    // Aws Lambda 함수에 Presigned Url 요청 -> 이미지 업로드 -> 이미지 경로 가져오기
    private suspend fun uploadImagesAndGetS3Urls(imageUris: List<Uri>): List<String> =
        coroutineScope {
            if (imageUris.isEmpty()) return@coroutineScope emptyList()  // 이미지 비었을 때


            val uploadJobs = imageUris.map { uri ->
                async {
                    try {
                        val fileName =  // 이미지 이름
                            "item-image-${System.currentTimeMillis()}-${uri.lastPathSegment}"

                        Log.d("imageUpload", "fileName : ${fileName}")

                        val presignedUrl =  // 이미지 저장 위치, 이름을 넣어서 PresignedUrl 값 가져오기
                            storageRepository.getPresignedUrl("item", fileName).getOrNull()

                        Log.d("imageUpload", "presignedUrl : ${presignedUrl}")


                        presignedUrl?.let {
                            val requestBody = context.contentResolver.openInputStream(uri)?.use {
                                it.readBytes().toRequestBody(
                                    context.contentResolver.getType(uri)?.toMediaTypeOrNull()
                                )
                            } ?: return@async null

                            Log.d("imageUpload", "presignedUrl : ${presignedUrl}")

                            // 이미지 업로드
                            val uploadResult = storageRepository.uploadFile(it, requestBody)

                            Log.d("imageUpload", "이미지 업로드 결과 : ${uploadResult}")


                            if (uploadResult.isSuccess) {
                                it.substringBefore("?")   // 서버 업로드 주소 url을 리스트에 담음
//                                fileName    // 파일 이름
                            } else null
                        }
                    } catch (e: Exception) {
                        Log.d("imageUpload", "에러 : ${e.message}")

                        null

                    }
                }
            }
            uploadJobs.awaitAll().filterNotNull()
        }

    // --- 아래 변환 함수들이 추가 ---
    private fun ItemDto.toUiModel(presignedUrl: String?): ItemUiModel {
        return ItemUiModel(
            id = this.itemId,
            title = this.title,
            location = this.location.address,
            timeAgo = formatTimeAgo(this.createdAt),
            price = this.price,
            imageUrl = presignedUrl ?: "",
            likes = this.favoriteCount,
            comments = 0,
            viewCount = this.viewCount
        )
    }


    /**
     * 시간 문자열을 "N년 전"과 같은 상대 시간으로 변환하는 함수
     */
    private fun formatTimeAgo(createdAt: String): String {
        return try {
            val createdTime = OffsetDateTime.parse(createdAt)
            val now = OffsetDateTime.now()

            // 년, 월, 일, 시, 분 단위로 시간 차이를 계산합니다.
            val years = ChronoUnit.YEARS.between(createdTime, now)
            val months = ChronoUnit.MONTHS.between(createdTime, now)
            val days = ChronoUnit.DAYS.between(createdTime, now)
            val hours = ChronoUnit.HOURS.between(createdTime, now)
            val minutes = ChronoUnit.MINUTES.between(createdTime, now)

            // [핵심 로직] 년 > 월 > 일 > 시간 > 분 순서로 확인합니다.
            when {
                years > 0 -> "${years}년 전"
                months > 0 -> "${months}달 전"
                days > 0 -> "${days}일 전"
                hours > 0 -> "${hours}시간 전"
                minutes > 0 -> "${minutes}분 전"
                else -> "방금 전"
            }
        } catch (e: Exception) {
            // 날짜 형식이 잘못되었을 경우를 대비한 예외 처리
            "시간 정보 없음"
        }
    }


//    private fun formatTimeAgo(createdAt: String): String {
//        return try {
//            val createdTime = OffsetDateTime.parse(createdAt)
//            val now = OffsetDateTime.now()
//
//            val minutes = ChronoUnit.MINUTES.between(createdTime, now)
//            val hours = ChronoUnit.HOURS.between(createdTime, now)
//            val days = ChronoUnit.DAYS.between(createdTime, now)
//
//            when {
//                minutes < 1 -> "방금 전"
//                minutes < 60 -> "${minutes}분 전"
//                hours < 24 -> "${hours}시간 전"
//                days < 7 -> "${days}일 전"
//                else -> "${createdTime.toLocalDate()}"
//            }
//        } catch (e: Exception) {
//            "시간 정보 없음"
//        }
//    }


    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ ItemDtail 데이터 가져오기 부분이 추가 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

    /**
     * [수정] ItemDetail을 가져올 때 Presigned URL을 함께 처리하도록 변경
     */
    override suspend fun getItemDetail(itemId: Int): ItemDetailModel {
        // 1. 메인 서버에서 아이템 상세 정보(S3 Object Key 포함)를 가져옵니다.
        val responseDto = itemApiService.getItemDetail(itemId)
        val itemData = responseDto.data

        // 2. 각 이미지의 Object Key로 Presigned URL을 병렬로 요청합니다.
        val viewableImageUrls = coroutineScope {
            val urlJobs = itemData.imageUrls.map { key ->
                async {
                    // S3 전체 URL에서 순수 객체 키만 파싱 (기존 로직과 동일)
                    val objectKey =
                        key.removePrefix("https://raon-market-images-prod.s3.ap-northeast-2.amazonaws.com/")
                    storageRepository.getPresignedImageUrl(objectKey).getOrNull()
                }
            }
            // 모든 Presigned URL 요청이 끝날 때까지 기다린 후, null이 아닌 것만 필터링
            urlJobs.awaitAll().filterNotNull()
        }

        // 3. 최종 UI 모델로 변환하여 반환합니다.
        // 이때, 기존 S3 Object Key 대신 방금 받은 Presigned URL 목록을 사용합니다.
        return itemData.toItemDetailModel(presignedUrls = viewableImageUrls)
    }

    /**
     *  toItemDetailModel 함수가 Presigned URL을 받도록 변경
     */
    private fun ItemDetailData.toItemDetailModel(presignedUrls: List<String>): ItemDetailModel {
        return ItemDetailModel(
            id = this.productId,
            imageUrls = presignedUrls, // S3 Object Key 대신 Presigned URL 사용
            sellerNickname = this.seller.nickname,
            sellerProfileUrl = this.seller.profileImage, // TODO: 판매자 프로필 이미지도 Presigned URL 처리가 필요하다면 추가
            sellerAddress = this.location.address,
            title = this.title,
            category = this.categories.joinToString(" > ") { it.name },
            createdAt = formatTimeAgo(this.createdAt),  // 날짜 받아서 과거형으로 변경
            description = this.description,
            favoriteCount = this.favoriteCount,
            viewCount = this.viewCount,
            price = this.price
        )
    }

    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲


    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 채팅 부분 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

    // 채팅방 확인 함수
    override suspend fun getChatRoomForItem(itemId: Long): ApiResult<GetChatRoomResponseDto> {
        return handleApi { itemApiService.getChatRoomForItem(itemId) }
    }

    // 채팅방 생성 함수
    override suspend fun createChatForItem(itemId: Long): ApiResult<CreateChatRoomResponseDto> {
        return handleApi { itemApiService.createChatForItem(itemId) }
    }

// ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲


    // [추가] 조회수 증가 함수 구현
    override suspend fun increaseViewCount(itemId: Int) {
        try {
            val response = itemApiService.increaseViewCount(itemId)
            if (response.isSuccessful) {
                Log.d("ItemRepositoryImpl", "View count for item $itemId increased successfully.")
            } else {
                Log.e(
                    "ItemRepositoryImpl",
                    "Failed to increase view count for item $itemId. Code: ${response.code()}"
                )
            }
        } catch (e: Exception) {
            // 네트워크 오류 등 예외 발생 시 로그 기록
            Log.e("ItemRepositoryImpl", "Error increasing view count for item $itemId", e)
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
package com.example.raon.features.item.z_data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.features.item.z_data.remote.api.ItemApiService
import com.example.raon.features.item.z_data.remote.dto.ItemRequest
import com.example.raon.features.item.z_data.remote.dto.ItemResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemApiService: ItemApiService,
    private val storageRepository: ImageStorageRepository,
    @ApplicationContext private val context: Context
) {
    // 새 item 게시글 등록
    suspend fun postNewItem(
        title: String,
        description: String,
        price: Int,
        imageUris: List<Uri>
    ): ItemResponse {

        try {
            Log.d("addItemTest", "S3에 이미지 업로드를 시작.")

            // StorageRepository를 사용하여 S3에 이미지를 업로드하고 최종 URL 목록을 받아오기
            val imageUrls = uploadImagesAndGetS3Urls(imageUris)
            if (imageUris.isNotEmpty() && imageUrls.isEmpty()) {

                Log.d("addItemTest", "S3 이미지 업로드 실패?. URL: ${imageUrls.toString()}")

                // 이미지를 업로드하려고 했지만 모두 실패한 경우
                return ItemResponse(
                    code = "IMAGE_UPLOAD_FAILED",
                    message = "이미지 업로드에 실패했습니다.",
                    data = null
                )
            }
            Log.d("addItemTest", "S3 이미지 업로드 완료. URL: $imageUrls")


            // 서버에 전송할 데이터 객체 : 받은 URL과 텍스트 데이터로 ItemRequest 객체를 만들어 최종 전송
            val itemRequest = ItemRequest(
                categoryId = 270, // TODO: 실제 앱 상태에서 값 가져오기
                locationId = 435, // TODO: 실제 앱 상태에서 값 가져오기
                title = title,
                description = description,
                price = 9000,
                condition = "USED", // TODO: 실제 앱 상태에서 값 가져오기
                tradeType = "DIRECT", // TODO: 실제 앱 상태에서 값 가져오기
                imageList = imageUrls
            )
            Log.d("addItemTest", "item 업로드 전")

            // 서버에 게시글 데이터 업로드
            val response = itemApiService.postItem(itemRequest)

            Log.d("addItemTest", "item 업로드 후")
            Log.d("addItemTest", "item 업로드 에러{${response.code}}")
            Log.d("addItemTest", "item 업로드 에러 메시지{${response.message}}")

            return response

        } catch (e: retrofit2.HttpException) {

            // 에러 응답 본문을 문자열로 변환
            val errorBody = e.response()?.errorBody()?.string()

            // Gson을 사용해 JSON 문자열을 ErrorResponse 객체로 파싱
            val gson = Gson()
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)

                // 파싱된 객체에서 code와 message를 추출하여 로그 출력
                Log.d("addItemTest", "API 에러 코드: ${errorResponse.code}")
                Log.d("addItemTest", "API 에러 메시지: ${errorResponse.message}")

                // 💡 errors 필드가 null이 아닐 경우, 각 항목을 로그로 출력
                errorResponse.errors?.forEach { (field, errorMessage) ->
                    Log.d("addItemTest", " - 필드: ${field}, 문제: ${errorMessage}")
                }


            } catch (jsonE: Exception) {
                // JSON 파싱 자체에 실패한 경우 (서버가 다른 형식의 에러를 보냈을 수 있음)
                Log.d("addItemTest", "에러 응답 파싱 실패: ${jsonE.message}")
                return ItemResponse(
                    code = "PARSING_ERROR",
                    message = "에러 응답을 파싱할 수 없습니다.",
                    data = null
                )
            }


            // 어떤 에러가 발생했는지 로그로 출력!
            Log.d("addItemTest", "item 업로드 실패: ${e.message}", e)
            Log.d("addItemTest", "item 업로드 실패: ${e.message}", e)


            // 실패했을 때 반환할 기본 ItemResponse 객체 (필요에 따라 수정)
            return ItemResponse(code = "ERROR", message = e.message ?: "Unknown error", data = null)

        }


    }

    /**
     * 이미지 업로드 함수
     * StorageRepository를 사용하여 여러 이미지를 S3에 동시에 업로드하고,
     * 성공한 이미지들의 영구 URL 목록을 반환
     */
    private suspend fun uploadImagesAndGetS3Urls(imageUris: List<Uri>): List<String> =
        coroutineScope {
            if (imageUris.isEmpty()) return@coroutineScope emptyList()

            // 각 이미지를 비동기(동시)로 업로드 처리
            val uploadJobs = imageUris.map { uri ->
                async {
                    try {
                        // 1단계: Presigned URL 요청
                        val fileName =
                            "item-image-${System.currentTimeMillis()}-${uri.lastPathSegment}"
                        val presignedUrlResult = storageRepository.getPresignedUrl("item", fileName)

                        Log.d("addItemTest", "presignedUrl 확인: ${presignedUrlResult.toString()}")


                        presignedUrlResult.getOrNull()?.let { presignedUrl ->
                            // 2단계: Uri를 RequestBody로 변환
                            val requestBody = context.contentResolver.openInputStream(uri)?.use {
                                it.readBytes().toRequestBody(
                                    context.contentResolver.getType(uri)?.toMediaTypeOrNull()
                                )
                            } ?: return@async null // 파일 읽기 실패 시 null 반환

                            // 3단계: Presigned URL로 실제 파일 업로드
                            val uploadResult =
                                storageRepository.uploadFile(presignedUrl, requestBody)

                            if (uploadResult.isSuccess) {
                                // 4단계: 성공 시, 영구 URL을 반환 (Presigned URL에서 '?' 앞부분)
                                presignedUrl.substringBefore("?")
                            } else {
                                null // 업로드 실패 시 null 반환
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ItemRepository", "이미지 업로드 중 오류 발생: $uri", e)
                        null // 예외 발생 시 null 반환
                    }
                }
            }

            // 모든 비동기 작업이 끝날 때까지 기다린 후, null이 아닌(성공한) URL들만 필터링하여 반환
            uploadJobs.awaitAll().filterNotNull()
        }
}


data class ErrorResponse(
    @SerializedName("code") // JSON 필드 이름과 변수 이름이 같으면 생략 가능
    val code: String,

    @SerializedName("message")
    val message: String,

    // 💡 이 부분을 추가하세요!
    @SerializedName("errors")
    val errors: Map<String, String>? // errors 필드가 없을 수도 있으므로 Nullable(?)로 선언
)
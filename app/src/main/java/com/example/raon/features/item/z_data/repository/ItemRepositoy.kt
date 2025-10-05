package com.example.raon.features.item.z_data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.raon.features.item.z_data.remote.api.ItemApiService
import com.example.raon.features.item.z_data.remote.dto.ItemRequest
import com.example.raon.features.item.z_data.remote.dto.ItemResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemApiService: ItemApiService,
    @ApplicationContext private val context: Context
) {
    // 새 item 게시글 등록
    suspend fun postNewItem(
        title: String,
        description: String,
        price: String,
        imageUris: List<Uri>
    ): ItemResponse {

        try {
            Log.d("addItemTest", "이미지 업로드 전")

            // 1단계: 이미지 Uri를 Multipart 파일로 변환하여 업로드하고 URL 받기
            val imageUrls = uploadImagesAndGetUrls(imageUris)

            Log.d("addItemTest", "이미지 업로드 후")
            Log.d("addItemTest", "이미지 URL: $imageUrls")


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
            Log.d("addItemTest", "item 업로드 전 ")
            Log.d("addItemTest", "이미지 url : ${itemRequest.imageList} ")


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

    private suspend fun uploadImagesAndGetUrls(imageUris: List<Uri>): List<String> {
        if (imageUris.isEmpty()) return emptyList()

        val imageParts = imageUris.mapNotNull { uri ->
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "images",
                    "image_${System.currentTimeMillis()}.jpg",
                    requestFile
                )
            }
        }
        // 실제로는 아래 코드를 실행해야 합니다.
        // return itemApiService.uploadImages(imageParts)

        // 지금은 테스트를 위해 임시 URL을 반환합니다.
//        return imageUris.map { "https://example.com/images/${System.nanoTime()}.jpg" }

        return listOf(
            "https://example.com/images/${System.nanoTime()}.jpg",
            "https://example.com/images/${System.nanoTime() + 1}.jpg"
        )
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
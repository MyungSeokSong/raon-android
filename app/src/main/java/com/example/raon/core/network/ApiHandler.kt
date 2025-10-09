package com.example.raon.core.network


import android.util.Log
import com.example.raon.core.network.model.ErrorResponseBody
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

// Retrofit API 호출을 안전하게 실행하고 결과를 ApiResult로 감싸주는 함수
// Repository에서 try-catch를 대신해 줄 헬퍼 함수
/**
 * Retrofit API 호출을 안전하게 실행하고 결과를 ApiResult로 감싸주는 함수
 * @param execute API 호출을 실행하는 suspend 람다 함수
 * @return ApiResult<T> 형태의 결과
 */
suspend fun <T> handleApi(
    execute: suspend () -> Response<T>
): ApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {

            Log.d("HandleApi", "API Success: Body - $body")

            ApiResult.Success(body)
        } else {
            Log.e("HandleApi", "API Error: Code=${response.code()}, Message=${response.message()}")

            // Http 200말고 다른 에러 code를 처리 함

            // 1. 에러 응답의 본문을 문자열로 읽어옵니다.
            val errorBodyString = response.errorBody()?.string()


            // 2. 읽어온 문자열을 ErrorResponseBody DTO로 파싱합니다.
            val errorResponse = try {
                Gson().fromJson(errorBodyString, ErrorResponseBody::class.java)
            } catch (e: Exception) {
                null // 파싱에 실패하면 null 처리
            }

            // 3. 수정된 ApiResult.Error 형식에 맞게 객체를 생성하여 반환합니다.
            ApiResult.Error(code = response.code(), errorBody = errorResponse)


//            ApiResult.Error(code = response.code(), message = response.message())
        }
    } catch (e: IOException) {
        Log.e("HandleApi", "API Exception (IOException): ${e.message}", e)

        // 네트워크 연결 오류 (인터넷 끊김 등)
        ApiResult.Exception(e)
    } catch (e: Exception) {
        Log.e("HandleApi", "API Exception (General): ${e.message}", e)

        // 기타 모든 예외 (JSON 파싱 오류 등)
        ApiResult.Exception(e)
    }
}

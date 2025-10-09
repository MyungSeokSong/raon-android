package com.example.raon.core.network

import com.example.raon.core.network.model.ErrorResponseBody

// T를 out으로 변경하여 공변성을 갖도록 수정
sealed class ApiResult<out T> {
    // 성공
    data class Success<out T>(val data: T) : ApiResult<T>()

    // API 에러 (HTTP 상태 코드가 200이 아님)
//    data class Error(val code: Int, val message: String?) : ApiResult<Nothing>()

    // String? 타입의 message 대신, ErrorResponseBody? 타입을 받도록 변경
    data class Error(val code: Int, val errorBody: ErrorResponseBody?) : ApiResult<Nothing>()


    // 네트워크 예외 등
    data class Exception(val e: Throwable) : ApiResult<Nothing>()

    // 성공 시에만 특정 동작을 수행하는 확장 함수
    suspend fun onSuccess(action: suspend (T) -> Unit): ApiResult<T> {
        if (this is Success) {
            action(data) // 이제 action은 suspend 람다이므로 내부에서 suspend 함수 호출 가능
        }
        return this
    }

    // 결과 타입을 다른 타입으로 변환하는 확장 함수
    fun <R> map(transform: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(code, errorBody)
            is Exception -> Exception(e)
        }
    }
}
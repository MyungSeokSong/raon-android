package com.example.raon.core.network

import android.util.Log
import com.example.raon.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authRepository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "Authenticator 호출됨! 401 에러 감지.")

        // 문제 1 해결: 올바른 재시도 횟수 확인
        if (responseCount(response) >= 2) {
            Log.d("TokenAuthenticator", "재시도 횟수 초과. 갱신을 중단합니다.")
            return null
        }

        synchronized(this) {
            val currentAccessToken = authRepository.getAccessTokenSync()
            val failedRequestToken =
                response.request.header("Authorization")?.substringAfter("Bearer ")

            if (failedRequestToken != null && currentAccessToken != failedRequestToken) {
                Log.d("TokenAuthenticator", "다른 스레드가 이미 토큰을 갱신함. 새 토큰으로 바로 재시도.")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            Log.d("TokenAuthenticator", "토큰 갱신을 시도합니다...")
            return runBlocking {
                try {
                    // 문제 2 해결: 실제 토큰 '재발급' 함수 호출
                    val newAccessToken = authRepository.refreshToken()

                    if (newAccessToken != null) {
                        Log.d("TokenAuthenticator", "토큰 갱신 성공! 새 토큰으로 재시도합니다.")
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        Log.e("TokenAuthenticator", "토큰 갱신 실패: refreshToken()이 null을 반환했습니다.")
//                        authRepository.logout() // Token을 다 지움
                        null
                    }
                } catch (e: Exception) {
                    Log.e("TokenAuthenticator", "토큰 갱신 중 예외 발생", e)
//                    authRepository.logout() // Token을 다 지움
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}
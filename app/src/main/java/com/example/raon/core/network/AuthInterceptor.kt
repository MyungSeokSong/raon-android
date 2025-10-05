package com.example.raon.core.network

import com.example.raon.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

// Hilt가 AuthRepository를 만들어서 주입해줄 수 있도록 생성자를 수정합니다.
class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository // Context를 직접 받는 대신, 완성된 AuthRepository를 주입받습니다.
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // 1. "No-Authentication" 헤더가 있는지 확인
        val request = chain.request()
        val noAuthHeader = request.header("No-Authentication")

        // 2. 헤더가 있다면, 해당 헤더를 제거하고 그대로 요청을 진행 (토큰 추가 X)
        if (noAuthHeader != null) {
            val newRequest = request.newBuilder()
                .removeHeader("No-Authentication")
                .build()
            return chain.proceed(newRequest)
        }

        // 3. 헤더가 없다면, 주입받은 authRepository에서 토큰을 가져와 요청에 추가
        //    이제 직접 만들 필요가 없습니다. Hilt가 이미 완벽하게 만들어준 것을 사용하면 됩니다.
        val accessToken = runBlocking { authRepository.getAccessToken().first() }

        val newRequest = if (!accessToken.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}


package com.example.raon.core.network

import android.content.Context
import com.example.raon.features.auth.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

// 모든 API 요청을 가로채, 헤더에 자동으로 Access Token을 추가하는 클래스
class AuthInterceptor @Inject constructor(
    // Hilt가 Context를 주입할 수 있도록 생성자에 @Inject 추가
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // SharedPreferences에서 저장된 토큰을 가져오기
        val token = TokenManager.getAccessToken(context)

        // if-else 표현식을 사용하여 조건에 따라 다른 요청 객체를 생성
        val newRequest = if (!token.isNullOrBlank()) {
            // 토큰이 있을 경우: 헤더를 추가해 새 요청을 빌드
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            // 토큰이 없을 경우, 그냥 요청
            chain.request()
        }

        // 최종적으로 만들어진 요청을 서버로 보내기
        return chain.proceed(newRequest)
    }
}
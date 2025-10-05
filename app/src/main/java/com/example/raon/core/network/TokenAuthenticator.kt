package com.example.raon.core.network

import android.content.Context
import com.example.raon.features.auth.data.remote.api.AuthApiService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

// API 요청이 401 에러로 실패했을 때, 토큰을 갱신하고 자동으로 요청을 재시도하는 클래스
// Access Token 만료 시, 자동으로 토큰 재발급 및 API 재요청을 처리하는 클래스
class TokenAuthenticator @Inject constructor(
    private val context: Context,
    private val authApiService: AuthApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 이전에 제공했던 토큰 재발급 및 재시도 로직을 여기에 구현합니다.
        // ...
        // 우선은 Interceptor만 먼저 적용해보는 것도 좋습니다.
        // 만약 이 부분을 구현하지 않으려면, 3단계에서 .authenticator(...) 부분은 빼고 진행하세요.
        return null // 임시
    }
}
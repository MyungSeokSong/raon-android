package com.example.raon.features.auth.data.repository

import android.content.Context
import android.util.Log
import com.example.raon.features.auth.data.local.TokenManager
import com.example.raon.features.auth.data.remote.api.AuthApiService
import com.example.raon.features.auth.data.remote.dto.LoginRequest
import com.example.raon.features.auth.ui.viewmodel.LoginResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// 역할: 서버 통신을 직접 실행하고 결과를 가공하여 LoginResult 형태로 반환

// @Inject construtor 사용 => 즉 Hilt 사용해서 context를 직접 주입 받지 않아도 됨
class AuthRepository @Inject constructor(

    // 애플리케이션 conext 사용
    @ApplicationContext private val context: Context,

    // ApiClient로부터 미리 생성된 authApiService 인스턴스를 주입받습니다.
    private val apiService: AuthApiService

    // ApiClient로부터 미리 생성된 authApiService 인스턴스를 주입받습니다.
//    private val apiService: AuthApiService = ApiClient.authApiService
) {
    suspend fun login(email: String, password: String): LoginResult {
        return try {

            // 통신 코드
            val request = LoginRequest(email, password) // 서버와 통신할 데이터
            val response = apiService.login(request)    // 서버와 통신한 결과 데이터


            // 서버와 통신 데이터 로그값
            Log.d("LoginTest", "서버 Repository 응답 전체: $response")
            if (response.isSuccessful) {
                // body
                Log.d("LoginTest", "서버 응답 내용 (Body): ${response.body().toString()}")
                // header
                Log.d("LoginTest", "서버 응답 헤더: ${response.headers()}")
            }


            // 서버와 통신이 성공 and body가 비어있지 않을 때
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!   // 서버 응답 데이터
                val accessToken = loginResponse.data.accessToken    // 토큰 데이터

                Log.d("LoginTest", "토큰 데이터: $accessToken")


                if (loginResponse.code == "OK" && !accessToken.isNullOrBlank()) { // -> 로그인 성공

                    // TokenManager를 사용해 Access Token을 기기에 저장
                    TokenManager.saveAccessToken(context, accessToken)

                    // Access Token 저장 확인 코드
                    val acessTokenChek = TokenManager.getAccessToken(context)
                    Log.d("LoginTest", "토큰 데이터 저장 확인!: $acessTokenChek")

                    // 헤더에서 Refresh Token 파싱 및 저장
                    val cookieHeader = response.headers()["Set-Cookie"]
                    if (cookieHeader != null) {
                        val refreshToken = cookieHeader.split(";")[0].split("=")[1]
                        TokenManager.saveRefreshToken(context, refreshToken)

                        // Access Token 저장 확인 코드
                        val refreshTokenChek = TokenManager.getRefreshToken(context)
                        Log.d("LoginTest", "Refresh Token 저장 성공: $refreshTokenChek")
                    }



                    LoginResult.Success(loginResponse.message)
                } else {    // -> 로그인 실패
                    LoginResult.Error(loginResponse.message)
                }
            } else { // -> 서버 통신 실패
                LoginResult.Error("로그인 실패 (서버 응답 오류: ${response.code()})")
            }


        } catch (e: Exception) { // -> 예외 발생
            Log.d("LoginTest", "서버 Repository 예외 발생: ${e.message}")

            LoginResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
        }
    }
}
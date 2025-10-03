package com.example.raon.features.auth.data.repository

import android.content.Context
import android.util.Log
import com.example.raon.features.auth.data.local.TokenManager
import com.example.raon.features.auth.data.remote.api.AuthApiService
import com.example.raon.features.auth.data.remote.dto.LoginRequest
import com.example.raon.features.auth.data.remote.dto.SignUpRequest
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

) {

    // 로그인
    suspend fun login(email: String, password: String): LoginResult {
        return try {

            // 통신 코드
            val request = LoginRequest(email, password) // 서버와 통신할 데이터
            val response = apiService.login(request)    // 서버와 통신한 결과 데이터

            when (response.code()) {
                // 1. HTTP 상태 코드가 200 (OK)인 경우
                200 -> {
                    if (response.body() != null) {

                        val loginResponse = response.body()!!   // 서버 응답 데이터
                        val accessToken = loginResponse.data.accessToken    // 토큰 데이터

                        // 서버와 통신 데이터 로그값
                        Log.d("LoginTest", "서버 응답 내용 (Body): ${response.body().toString()}") // body
                        Log.d("LoginTest", "서버 응답 헤더: ${response.headers()}")   // header
                        Log.d("LoginTest", "토큰 데이터: $accessToken")  // accessToken

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
                        } else {

                        }
                    } else {
                    }
                }

                401 -> {
                    Log.d("LoginTest", "로그인 실패")
                    LoginResult.Faliure("로그인 실패 (401 Unauthorized)")
                }

                else -> {}
            }


        } catch (e: Exception) { // -> 예외 발생

            Log.d("LoginTest", "서버 통신 실패 : ${e.message}")

            LoginResult.ServerError(e.message ?: "로그인 실패 (서버 응답 오류: ${e.message})")
        } as LoginResult
    }

    // 회원가입
    suspend fun signup(nickname: String, email: String, password: String, locationId: Int?) {

        // 통신 코드
        val request = SignUpRequest(nickname, email, password, 1760) // 서버와 통신할 데이터
        val response = apiService.signUp(request)    // 서버와 통신한 결과 데이터


        // 서버와 통신 데이터 로그값
        Log.d("SignupTest", "서버 응답 내용 (code): ${response.code()}") // body
        Log.d("SignupTest", "서버 응답 내용 (Body): ${response.body().toString()}") // body
        Log.d("SignupTest", "서버 응답 헤더: ${response.headers()}")   // header
//        Log.d("SignupTest", "토큰 데이터: $accessToken")  // accessToken

    }
}
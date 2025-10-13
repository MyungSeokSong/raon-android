package com.example.raon.features.auth.data.repository

import android.util.Log
import com.example.raon.features.auth.data.local.TokenManager
import com.example.raon.features.auth.data.remote.api.AuthApiService
import com.example.raon.features.auth.data.remote.dto.LoginRequest
import com.example.raon.features.auth.data.remote.dto.SignUpRequest
import com.example.raon.features.auth.ui.viewmodel.LoginResult
import com.example.raon.features.auth.ui.viewmodel.SignUpResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.CookieManager
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val cookieManager: CookieManager
) {

    private var currentAccessToken: String? = null

    init {
        currentAccessToken = tokenManager.getAccessToken()
        Log.d("AuthRepository", "초기 토큰 로드 완료: $currentAccessToken")
    }

    fun getAccessTokenSync(): String? {
        return currentAccessToken
    }

    fun getAccessToken(): Flow<String?> = flow {
        emit(tokenManager.getAccessToken())
    }

    fun getRefreshToken(): Flow<String?> = flow {
        emit(tokenManager.getRefreshToken())
    }

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            when (response.code()) {
                200 -> {
                    if (response.body() != null) {
                        val loginResponse = response.body()!!
                        val accessToken = loginResponse.data.accessToken

                        if (loginResponse.code == "OK" && !accessToken.isNullOrBlank()) {
                            tokenManager.saveAccessToken(accessToken)
                            currentAccessToken = accessToken

                            val cookieHeader = response.headers()["Set-Cookie"]
                            if (cookieHeader != null) {
                                val refreshToken = cookieHeader.split(";")[0].split("=")[1]
                                tokenManager.saveRefreshToken(refreshToken)
                            }
                            LoginResult.Success(loginResponse.message)
                        } else {
                            // ✅ 오타 수정
                            LoginResult.Failure(loginResponse.message)
                        }
                    } else {
                        // ✅ 오타 수정
                        LoginResult.Failure("응답 바디가 비어있습니다.")
                    }
                }

                401 -> {
                    Log.d("LoginTest", "로그인 실패")
                    // ✅ 오타 수정
                    LoginResult.Failure("로그인 실패 (401 Unauthorized)")
                }

                else -> {
                    // ✅ 오타 수정
                    LoginResult.Failure("알 수 없는 오류: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.d("LoginTest", "서버 통신 실패 : ${e.message}")
            LoginResult.ServerError(e.message ?: "로그인 실패 (서버 응답 오류)")
        }
    }

    suspend fun signup(
        nickname: String,
        email: String,
        password: String,
        locationId: Int
    ): LoginResult {
        return try {
            val request = SignUpRequest(nickname, email, password, locationId)
            val response = apiService.signUp(request)

            when (response.code()) {
                200, 201 -> {
                    if (response.body() != null) {
                        val signUpResponse = response.body()!!
                        val accessToken = signUpResponse.data.accessToken

                        if (signUpResponse.code == "OK" && !accessToken.isNullOrBlank()) {
                            tokenManager.saveAccessToken(accessToken)
                            currentAccessToken = accessToken
                            val cookieHeader = response.headers()["Set-Cookie"]
                            if (cookieHeader != null) {
                                val refreshToken = cookieHeader.split(";")[0].split("=")[1]
                                tokenManager.saveRefreshToken(refreshToken)
                            }
                            SignUpResult.Success(signUpResponse.message)
                        } else {
                            // ✅ 오타 수정
                            SignUpResult.Failure(signUpResponse.message)
                        }
                    } else {
                        // ✅ 오타 수정
                        SignUpResult.Failure("회원가입 실패: 응답 바디가 비어있습니다.")
                    }
                }

                409 -> {
                    // ✅ 오타 수정
                    SignUpResult.Failure("이미 가입된 이메일입니다.")
                }

                else -> {
                    // ✅ 오타 수정
                    SignUpResult.Failure("회원가입 실패 (코드: ${response.code()})")
                }
            }
        } catch (e: Exception) {
            Log.e("SignupTest", "서버 통신 실패 : ${e.message}")
            SignUpResult.ServerError(e.message ?: "회원가입 중 오류가 발생했습니다.")
        }
    }

    fun logout() {
        Log.d("Logout_Test", "--- Access Token 삭제 전 ---")
        val accessTokenBefore = tokenManager.getAccessToken()
        Log.d("Logout_Test", "저장된 Access Token: $accessTokenBefore")

        tokenManager.clearTokens()
        currentAccessToken = null

        Log.d("Logout_Test", "--- Access Token 삭제 후 ---")
        val accessTokenAfter = tokenManager.getAccessToken()
        Log.d("Logout_Test", "남아있는 Access Token: $accessTokenAfter")

        Log.d("Logout_Test", "--- 쿠키(Refresh Token) 삭제 전 ---")
        val cookiesBefore = cookieManager.cookieStore.cookies
        if (cookiesBefore.isEmpty()) {
            Log.d("Logout_Test", "저장된 쿠키가 없습니다.")
        } else {
            Log.d("Logout_Test", "저장된 쿠키: $cookiesBefore")
        }

        cookieManager.cookieStore.removeAll()

        Log.d("Logout_Test", "--- 쿠키(Refresh Token) 삭제 후 ---")
        val cookiesAfter = cookieManager.cookieStore.cookies
        Log.d("Logout_Test", "남아있는 쿠키: $cookiesAfter")

        Log.d("Logout_Test", "로그아웃 절차 완료.")
    }

    suspend fun refreshToken(): String? {
        Log.d("AuthRepository", "토큰 재발급 API 호출 시도...")
        return try {
            val response = apiService.refreshToken()

            if (response.code == "OK" && response.data?.accessToken != null) {
                val newAccessToken = response.data.accessToken
                currentAccessToken = newAccessToken
                tokenManager.saveAccessToken(newAccessToken)
                Log.d("AuthRepository", "토큰 재발급 성공! 새 토큰 저장 완료: $newAccessToken")
                newAccessToken
            } else {
                Log.e("AuthRepository", "API 응답 실패: ${response.message}")
                logout()
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "토큰 재발급 중 예외 발생", e)
            logout()
            null
        }
    }
}
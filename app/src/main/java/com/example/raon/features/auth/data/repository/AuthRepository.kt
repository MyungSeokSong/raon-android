package com.example.raon.features.auth.data.repository

import android.util.Log
import com.example.raon.features.auth.data.local.TokenManager
import com.example.raon.features.auth.data.remote.api.AuthApiService
import com.example.raon.features.auth.data.remote.dto.LoginRequest
import com.example.raon.features.auth.data.remote.dto.SignUpRequest
import com.example.raon.features.auth.ui.viewmodel.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.CookieManager
import javax.inject.Inject

// 역할: 서버 통신을 직접 실행하고 결과를 가공하여 LoginResult 형태로 반환
// @Inject construtor 사용 => 즉 Hilt 사용해서 context를 직접 주입 받지 않아도 됨
class AuthRepository @Inject constructor(
    // ApiClient로부터 미리 생성된 authApiService 인스턴스를 주입받습니다.
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val cookieManager: CookieManager

    // 로그인시 바로 User 데이터를 요청해서 가져오기 위해 사용
//    private val userRepository: UserRepository
) {

    // --- 아래 3개의 코드 블록을 추가합니다 ---
    // 1. 메모리에 현재 액세스 토큰을 저장할 변수
    private var currentAccessToken: String? = null

    // 2. Repository가 처음 생성될 때, 저장된 토큰을 읽어와 메모리에 미리 로드
    init {
        // 앱이 시작될 때 한 번만 실행됨
        currentAccessToken = tokenManager.getAccessToken()
        Log.d("AuthRepository", "초기 토큰 로드 완료: $currentAccessToken")
    }

    // 3. Interceptor와 Authenticator가 호출할 빠르고 안전한 동기 함수
    fun getAccessTokenSync(): String? {
        return currentAccessToken
    }


    // Access Token을 Flow 형태로 제공하는 함수
    // AuthInterceptor가 이 함수를 호출하여 토큰을 가져옴
    fun getAccessToken(): Flow<String?> = flow {
        emit(tokenManager.getAccessToken())
    }

    // Refresh Token을 Flow 형태로 제공하는 함수
    // TokenAuthenticator가 이 함수를 호출하여 토큰을 가져옴
    fun getRefreshToken(): Flow<String?> = flow {
        emit(tokenManager.getRefreshToken())
    }

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
                            tokenManager.saveAccessToken(accessToken)

                            currentAccessToken = accessToken // 메모리 캐시 업데이트


                            // Access Token 저장 확인 코드
                            val acessTokenChek = tokenManager.getAccessToken()
                            Log.d("LoginTest", "토큰 데이터 저장 확인!: $acessTokenChek")

                            // 헤더에서 Refresh Token 파싱 및 저장
                            val cookieHeader = response.headers()["Set-Cookie"]
                            if (cookieHeader != null) {
                                val refreshToken = cookieHeader.split(";")[0].split("=")[1]
                                tokenManager.saveRefreshToken(refreshToken)

                                // Access Token 저장 확인 코드
                                val refreshTokenChek = tokenManager.getRefreshToken()
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
    suspend fun signup(nickname: String, email: String, password: String, locationId: Int) {

        // 통신 코드
        val request = SignUpRequest(nickname, email, password, locationId) // 서버와 통신할 데이터
        val response = apiService.signUp(request)    // 서버와 통신한 결과 데이터


        // 서버와 통신 데이터 로그값
        Log.d("SignupTest", "서버 응답 내용 (code): ${response.code()}") // body
        Log.d("SignupTest", "서버 응답 내용 (Body): ${response.body().toString()}") // body
        Log.d("SignupTest", "서버 응답 헤더: ${response.headers()}")   // header
//        Log.d("SignupTest", "토큰 데이터: $accessToken")  // accessToken

    }

    // 로그아웃
    fun logout() {
        // --- 1. SharedPreferences (Access Token) 삭제 확인 ---
        Log.d("Logout_Test", "--- Access Token 삭제 전 ---")
        val accessTokenBefore = tokenManager.getAccessToken()
        Log.d("Logout_Test", "저장된 Access Token: $accessTokenBefore")

        // 실제 삭제 로직
        tokenManager.clearTokens()

        currentAccessToken = null // 메모리 캐시 초기화


        Log.d("Logout_Test", "--- Access Token 삭제 후 ---")
        val accessTokenAfter = tokenManager.getAccessToken()
        // 이 로그에서는 null이 출력되어야 정상입니다.
        Log.d("Logout_Test", "남아있는 Access Token: $accessTokenAfter")


        // --- 2. CookieManager (Refresh Token) 삭제 확인 ---
        Log.d("Logout_Test", "--- 쿠키(Refresh Token) 삭제 전 ---")
        val cookiesBefore = cookieManager.cookieStore.cookies
        if (cookiesBefore.isEmpty()) {
            Log.d("Logout_Test", "저장된 쿠키가 없습니다.")
        } else {
            // 저장된 쿠키 리스트를 모두 출력합니다.
            Log.d("Logout_Test", "저장된 쿠키: $cookiesBefore")
        }

        // 실제 삭제 로직
        cookieManager.cookieStore.removeAll()

        Log.d("Logout_Test", "--- 쿠키(Refresh Token) 삭제 후 ---")
        val cookiesAfter = cookieManager.cookieStore.cookies
        // 이 로그에서는 빈 리스트([])가 출력되어야 정상입니다.
        Log.d("Logout_Test", "남아있는 쿠키: $cookiesAfter")


        Log.d("Logout_Test", "로그아웃 절차 완료.")
    }


    // AccessToken 재발급
    /**
     * [추가] 실제 토큰 재발급 로직
     * @return 성공 시 새로운 Access Token, 실패 시 null
     */
    suspend fun refreshToken(): String? {
        // CookieJar가 자동으로 리프레시 토큰을 담아 요청합니다.
        Log.d("AuthRepository", "토큰 재발급 API 호출 시도...")
        return try {
            val response = apiService.refreshToken() // 서버 API 호출

            // 서버 응답이 성공적이고, 새로운 accessToken이 있다면
            if (response.code == "OK" && response.data?.accessToken != null) {
                val newAccessToken = response.data.accessToken

                currentAccessToken = newAccessToken // [추가] 메모리 캐시 업데이트


                // 1. 새로 받은 토큰을 기기에 저장
                tokenManager.saveAccessToken(newAccessToken)

                Log.d("AuthRepository", "토큰 재발급 성공! 새 토큰 저장 완료: $newAccessToken")

                // 2. TokenAuthenticator가 사용할 수 있도록 새 토큰을 반환
                newAccessToken
            } else {
                Log.e("AuthRepository", "API 응답 실패: ${response.message}")
                logout() // 재발급 실패 시 강제 로그아웃
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "토큰 재발급 중 예외 발생", e)
            logout() // 재발급 실패 시 강제 로그아웃
            null
        }
    }


}
package com.example.raon.core.di

import android.content.Context
import com.example.raon.core.network.AuthInterceptor
import com.example.raon.core.network.TokenAuthenticator
import com.example.raon.core.network.api.ImageStorageService
import com.example.raon.features.auth.data.remote.api.AuthApiService
import com.example.raon.features.auth.data.repository.AuthRepository
import com.example.raon.features.item.z_data.remote.api.ItemApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // --- 서버별 기본 URL ---
//    private const val RAON_SERVER_URL = "http://10.0.2.2:4000/" // 에뮬레이터용
    private const val RAON_SERVER_URL = "http://192.168.12.70:4000/" // 실제 앱 용


    // 잊지 말고 API Gateway ID를 꼭 수정해주세요!
    private const val AWS_API_GATEWAY_URL =
        "https://ruk7dmwn72.execute-api.ap-northeast-2.amazonaws.com/"


    // =================================================================
    // MARK: - 공통 컴포넌트 제공
    // =================================================================

    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        return JavaNetCookieJar(CookieManager())
    }

    // AuthInterceptor는 Hilt로부터 AuthRepository를 주입받습니다.
    @Provides
    @Singleton
    fun provideAuthInterceptor(authRepository: AuthRepository): AuthInterceptor {
        return AuthInterceptor(authRepository)
    }


    // =================================================================
    // MARK: - 토큰 자동 갱신 (순환 참조 해결 구조)
    // =================================================================

    // 401 에러 시 토큰 자동 갱신 로직
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        @ApplicationContext context: Context,
        authApiService: AuthApiService // Hilt가 아래에서 만든 '토큰 갱신 전용' 서비스를 주입합니다.
    ): TokenAuthenticator {
        return TokenAuthenticator(context, authApiService)
    }

    // '토큰 갱신 전용' OkHttpClient (Authenticator가 없어 순환 참조를 방지)
    @Provides
    @Singleton
    @Named("RefreshClient")
    fun provideRefreshOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(cookieJar)
            .build()
    }

    // '토큰 갱신 전용' Retrofit
    @Provides
    @Singleton
    @Named("RefreshRetrofit")
    fun provideRefreshRetrofit(@Named("RefreshClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RAON_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // =================================================================
    // MARK: - 메인 OkHttpClient 및 Retrofit 제공
    // =================================================================

    // '일반 API 호출용' 메인 OkHttpClient
    @Provides
    @Singleton
    @Named("MainClient")
    fun provideOkHttpClient(
        cookieJar: CookieJar,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authInterceptor)
            .cookieJar(cookieJar)
            .authenticator(tokenAuthenticator) // 401 에러 시 작동
            .build()
    }

    // 라온마켓 서버용 Retrofit
    @Provides
    @Singleton
    @Named("RaonRetrofit")
    fun provideRaonRetrofit(@Named("MainClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RAON_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 이미지 서버(AWS)용 Retrofit
    @Provides
    @Singleton
    @Named("ImageStorageRetrofit")
    fun provideImageStorageRetrofit(@Named("MainClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AWS_API_GATEWAY_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // =================================================================
    // MARK: - API 서비스 제공
    // =================================================================

    // 인증 API (토큰 갱신 전용 Retrofit 사용)
    @Provides
    @Singleton
    fun provideAuthApiService(@Named("RefreshRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // 아이템 API (라온마켓 서버용 Retrofit 사용)
    @Provides
    @Singleton
    fun provideItemApiService(@Named("RaonRetrofit") retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }

    // 이미지 저장 API (이미지 서버용 Retrofit 사용)
    @Provides
    @Singleton
    fun provideImageStorageService(@Named("ImageStorageRetrofit") retrofit: Retrofit): ImageStorageService {
        return retrofit.create(ImageStorageService::class.java)
    }
}


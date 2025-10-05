package com.example.raon.core.di

import android.content.Context
import com.example.raon.core.network.AuthInterceptor
import com.example.raon.core.network.TokenAuthenticator
import com.example.raon.features.auth.data.remote.api.AuthApiService
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
import javax.inject.Named // Named import 추가
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:4000/"

    // 액세스 토큰 헤더 자동 추가
    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context): AuthInterceptor {
        return AuthInterceptor(context)
    }

    // 리프레시 토큰용 쿠키 관리
    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        return JavaNetCookieJar(CookieManager())
    }

    // -------------------- ⚠️ 순환 참조 해결을 위한 전용 코드 시작 --------------------

    // 토큰 재발급용 OkHttpClient (이름으로 구분, Authenticator 없음)
    @Provides
    @Singleton
    @Named("RefreshClient")
    fun provideRefreshOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieJar)
            .build()
    }

    // 토큰 재발급용 Retrofit
    @Provides
    @Singleton
    @Named("RefreshRetrofit")
    fun provideRefreshRetrofit(@Named("RefreshClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 토큰 재발급 전용 AuthApiService
    @Provides
    @Singleton
    fun provideRefreshAuthApiService(@Named("RefreshRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // 401 에러 시 토큰 자동 갱신 (재발급용 AuthApiService 사용)
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        @ApplicationContext context: Context,
        authApiService: AuthApiService // Hilt가 위에서 만든 재발급 전용 서비스를 주입
    ): TokenAuthenticator {
        return TokenAuthenticator(context, authApiService)
    }

    // -------------------- ⚠️ 순환 참조 해결을 위한 전용 코드 끝 --------------------


    // -------------------- ✅ 일반 API 호출용 메인 코드 시작 --------------------

    // 네트워크 통신 설정 최종 조립
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieJar: CookieJar,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .cookieJar(cookieJar)
            .authenticator(tokenAuthenticator)
            .build()
    }

    // OkHttpClient로 레트로핏 생성
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 아이템 관련 API 서비스 (이제 메인 Retrofit을 사용)
    @Provides
    @Singleton
    fun provideItemApiService(retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }

    // -------------------- ✅ 일반 API 호출용 메인 코드 끝 --------------------
}
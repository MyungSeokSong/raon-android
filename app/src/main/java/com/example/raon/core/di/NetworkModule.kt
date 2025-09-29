package com.example.raon.core.di

import com.example.raon.features.auth.data.remote.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar // 추가
import okhttp3.JavaNetCookieJar // 추가
import okhttp3.OkHttpClient // 추가
import okhttp3.logging.HttpLoggingInterceptor // 추가 (통신 로그 확인용)
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager // 추가
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.20.229:4000/"

    // 1. CookieJar 제공자 추가
    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        // 인메모리(in-memory) 쿠키 저장소 생성
        return JavaNetCookieJar(CookieManager())
    }

    // 2. OkHttpClient 제공자 추가 (CookieJar와 로깅 인터셉터 포함)
    @Provides
    @Singleton
    fun provideOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // BODY 수준으로 로그를 남김
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieJar) // ✨ CookieJar를 OkHttp 클라이언트에 연결
            .build()
    }

    // 3. Retrofit 제공자 수정 (OkHttpClient를 주입받도록 변경)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ✨ CookieJar가 설정된 OkHttpClient를 Retrofit에 적용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 이 부분은 변경할 필요 없습니다.
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
package com.example.raon.features.bottom_navigation.b_test.di

import com.example.raon.features.bottom_navigation.b_test.data.remote.RetrofitApiTest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val BASE_URL = "http://10.80.8.78:8080/"

    @Provides
    @Singleton
    fun provideRetrofitApiService(retrofit: Retrofit): RetrofitApiTest {
        return retrofit.create(RetrofitApiTest::class.java)
    }

    // Retrofit 인스턴스를 제공하는 메서드 (이것은 필요합니다)
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // 실제 API URL로 변경하세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
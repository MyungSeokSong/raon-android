package com.example.raon.core.network


import com.example.raon.features.auth.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 인스턴스를 생성하고 관리하는 싱글톤 객체.
 * 앱의 모든 네트워크 통신을 위한 기반을 설정합니다.
 */


// object를 사용해서 싱글턴으로 사용하고 전역에서 접근가능
object ApiClient {

    // 통신할 서버의 기본 주소
    private const val BASE_URL = "http://192.168.144.57:4000/" // Postman에서 확인된 주소로 변경

    // Retrofit 인스턴스. lazy 초기화를 통해 처음 사용할 때 한 번만 생성됩니다.
    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL) // 서버 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> Kotlin 데이터 클래스 변환기 설정
            .build()
    }

    // 기능별로 만든 Service를 밑에 추가해서 사용함

    // AuthApiService 구현체를 앱 전체에서 단 하나만 생성하여 재사용
    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)

    }
}
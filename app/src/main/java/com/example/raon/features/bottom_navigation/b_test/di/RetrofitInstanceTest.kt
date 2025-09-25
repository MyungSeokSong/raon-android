package com.example.raon.features.bottom_navigation.b_test.di

import com.example.raon.features.bottom_navigation.b_test.data.remote.RetrofitApiTest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// object를 사용해서 싱글턴으로 사용하고 전역에서 접근가능
object RetrofitInstanceTest {


    //    private val BASE_URL = BuildConfig.BASE_URL
    private const val BASE_URL = "http://localhost:8080/items"

    val api: RetrofitApiTest by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApiTest::class.java)

    }

}


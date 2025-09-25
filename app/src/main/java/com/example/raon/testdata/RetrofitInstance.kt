package com.example.raon.testdata

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// object를 사용해서 싱글턴으로 사용하고 전역에서 접근가능
object RetrofitInstance {

//    private val BASE_URL = BuildConfig.BASE_URL

    private const val BASE_URL = "http://localhost:8080/items"

    val api: RetrofitApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApi::class.java)

    }


}


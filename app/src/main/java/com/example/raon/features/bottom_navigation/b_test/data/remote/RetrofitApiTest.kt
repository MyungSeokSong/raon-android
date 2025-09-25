package com.example.raon.features.bottom_navigation.b_test.data.remote

import com.example.raon.features.bottom_navigation.b_test.data.model.TestDTO
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitApiTest {

    @GET("items")
    suspend fun getItems(): Response<List<TestDTO>>

}
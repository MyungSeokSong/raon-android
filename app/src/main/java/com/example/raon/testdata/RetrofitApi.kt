package com.example.raon.testdata

import retrofit2.Response
import retrofit2.http.GET

interface RetrofitApi {

    @GET("items")
    suspend fun getItems(): Response<List<ItemDTO>>

}
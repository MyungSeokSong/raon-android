package com.example.raon.testdata

import retrofit2.Response

class Repository {

    suspend fun getItems(): Response<List<ItemDTO>> {
        return RetrofitInstance.api.getItems()


    }

}
package com.example.raon.features.bottom_navigation.b_test.data.repository

import com.example.raon.features.bottom_navigation.b_test.data.model.TestDTO
import com.example.raon.features.bottom_navigation.b_test.data.remote.RetrofitApiTest
import retrofit2.Response
import javax.inject.Inject

class RepositoryTest @Inject constructor(private val retrofitApiTest: RetrofitApiTest) {

    suspend fun getItem(): Response<List<TestDTO>> {
        return retrofitApiTest.getItems()
    }
}
package com.example.raon.features.location.domain.repository

import com.example.raon.features.location.domain.model.Location

interface LocationRepository {

    // Result 클래스를 사용해 성공/실패를 명확하게 전달
    suspend fun getLocations(keyword: String): Result<List<Location>>

}


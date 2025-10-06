package com.example.raon.features.user.domain.repository

import com.example.raon.core.network.ApiResult
import com.example.raon.features.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // DataStore에서 사용자 프로필을 Flow 형태로 가져옴
    fun getUserProfile(): Flow<User?>

    // 서버에서 프로필을 가져와 DataStore에 업데이트
    suspend fun fetchAndSaveUserProfile(): ApiResult<Unit>
}
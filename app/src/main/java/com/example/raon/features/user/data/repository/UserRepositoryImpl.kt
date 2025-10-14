package com.example.raon.features.user.data.repository


import android.util.Log
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.handleApi
import com.example.raon.features.user.data.local.UserDataStore
import com.example.raon.features.user.data.remote.UserApiService
import com.example.raon.features.user.domain.model.User
import com.example.raon.features.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val userDataStore: UserDataStore
) : UserRepository {

    // DataStore에서 Profile 데이터 가져오기
    override fun getUserProfile(): Flow<User?> {
        return userDataStore.userProfileFlow
    }

    override suspend fun fetchAndSaveUserProfile(): ApiResult<Unit> {
        return handleApi { userApiService.getProfile() }.onSuccess { response ->
            // API 호출 성공 시 DTO를 Domain Model로 변환
            val user = User(
                userId = response.data.userId,
                nickname = response.data.nickname,
                email = response.data.email,
                profileImage = response.data.profileImage,
                address = response.data.location.address,
                locationId = response.data.location.locationId
            )

            Log.d("UserData", "UserData 확인1 : ${response.data.userId},")
            Log.d("UserData", "UserData 확인2 : ${response.data.nickname},")
            Log.d("UserData", "UserData 확인3 : ${response.data.email},")
            Log.d("UserData", "UserData 확인4 : ${response.data.profileImage},")
            Log.d("UserData", "UserData 확인5 : ${response.data.location.address},")
            Log.d("UserData", "UserData 확인5 : ${response.data.location.locationId},")


            // DataStore에 저장
            userDataStore.saveUserProfile(user)


            // [확인용 코드] 저장 직후 바로 꺼내서 로그 찍기
            val savedUser = userDataStore.userProfileFlow.first()
            if (savedUser != null) {
                Log.d("DataStoreVerify", "✅ [확인 완료] 저장 직후 꺼내온 데이터: $savedUser")
            } else {
                Log.e("DataStoreVerify", "❌ [확인 실패] 저장 직후 데이터를 꺼낼 수 없습니다.")
            }
        }.map { } // 결과를 Unit으로 변환
    }
}
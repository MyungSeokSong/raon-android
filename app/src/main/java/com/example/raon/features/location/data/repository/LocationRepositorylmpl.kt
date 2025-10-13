package com.example.raon.features.location.data.repository

// LocationRepositoryImpl.kt

import com.example.raon.core.network.ApiResult
// ✨ 1. handleApi 함수를 import 합니다. (실제 경로에 맞게 수정)
import com.example.raon.core.network.handleApi
import com.example.raon.features.location.data.remote.api.LocationApiService
import com.example.raon.features.location.domain.model.Location
import com.example.raon.features.location.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val apiService: LocationApiService
) : LocationRepository {

    override suspend fun getLocations(keyword: String): Result<List<Location>> {
        // ✨ 2. [가장 중요한 수정] apiService 호출을 handleApi 헬퍼 함수로 감싸줍니다.
        // handleApi는 Retrofit의 Response<T>를 받아서 우리가 정의한 ApiResult<T>로 변환해줍니다.
        val apiResult = handleApi {
            apiService.getLocations(keyword, 20, 0)
        }

        // ✨ 3. when 문 이하의 로직은 거의 그대로 유지됩니다.
        // 이제 apiResult는 ApiResult<ApiResponse<LocationDataDto>> 타입입니다.
        return when (apiResult) {
            is ApiResult.Success -> {
                // apiResult.data는 ApiResponse<LocationDataDto> 타입이 됩니다.
                val locationData = apiResult.data.data
                if (locationData != null) {
                    val locations = locationData.locations.map { dto ->
                        // Location 모델의 파라미터 이름(id)에 맞게 수정
                        Location(locationId = dto.locationId, address = dto.address)
                    }
                    Result.success(locations)
                } else {
                    Result.failure(Exception("응답 데이터가 비어있습니다."))
                }
            }

            is ApiResult.Error -> {
                // handleApi에서 파싱한 에러 메시지를 사용하도록 수정
                val errorMessage = apiResult.errorBody?.message ?: "API 오류 (코드: ${apiResult.code})"
                Result.failure(Exception(errorMessage))
            }

            is ApiResult.Exception -> {
                Result.failure(apiResult.e)
            }
        }
    }
}
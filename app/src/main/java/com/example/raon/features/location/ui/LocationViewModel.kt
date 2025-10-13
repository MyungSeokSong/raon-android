package com.example.raon.features.location.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.location.domain.model.Location
import com.example.raon.features.location.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✨ 1. 리스트 아이템을 표현할 데이터 클래스 정의
//data class LocationInfo(
//    val mainAddress: String,
//    val subAddress: String
//)

// ✨ 2. Sealed Class가 LocationInfo 리스트를 갖도록 수정
sealed class LocationUiState {
    data object Loading : LocationUiState()
    data class Success(val locations: List<Location>) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,       // GPS 사용시 Context가 필요해서 사용됨
    private val locationRepository: LocationRepository
) : AndroidViewModel(application) {

    // LocationUiState 객체
    private val _locationUiState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val locationUiState = _locationUiState.asStateFlow()


    // 주소 검색어를 관리할 변수
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    init {
//        fetchNearbyLocations()
        observeSearchQuery()    // 검색어 관찰 함수
    }


    // 검색어 관찰 함수
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                // 500ms 동안 입력이 없으면 다음으로 진행 (API 호출 제어)
                .debounce(500L)
                // 이전과 동일한 검색어는 무시
                .distinctUntilChanged()
                // 새로운 검색어가 들어오면 이전 검색 작업을 취소하고 최신 검색만 실행
                .mapLatest { query ->
                    // 로딩 상태를 먼저 보여줌
                    _locationUiState.value = LocationUiState.Loading

                    // Repository를 통해 실제 데이터를 가져옴
                    locationRepository.getLocations(query)
                }
                .collect { result ->
                    // Repository에서 받은 Result를 UI State로 변환
                    result
                        .onSuccess { domainLocations: List<Location> ->

                            val uilocaions = domainLocations.map { location ->
                                Location(
                                    locationId = location.locationId,
                                    address = location.address
                                )

                            }
                            _locationUiState.value = LocationUiState.Success(uilocaions)
                        }
                        .onFailure { throwable ->
                            _locationUiState.value = LocationUiState.Error(
                                throwable.message ?: "오류가 발생했습니다."
                            )
                        }
                }
        }
    }

    // ✨ 4. UI에서 검색어가 변경될 때 호출할 함수
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }


}
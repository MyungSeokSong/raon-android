package com.example.raon.features.location.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ✨ 1. 리스트 아이템을 표현할 데이터 클래스 정의
data class LocationInfo(
    val mainAddress: String,
    val subAddress: String
)

// ✨ 2. Sealed Class가 LocationInfo 리스트를 갖도록 수정
sealed class LocationListUiState {
    data object Loading : LocationListUiState()
    data class Success(val locations: List<LocationInfo>) : LocationListUiState()
    data class Error(val message: String) : LocationListUiState()
}

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val _listUiState = MutableStateFlow<LocationListUiState>(LocationListUiState.Loading)
    val listUiState = _listUiState.asStateFlow()

    init {
        fetchNearbyLocations()
    }

    fun fetchNearbyLocations() {
        viewModelScope.launch {
            _listUiState.value = LocationListUiState.Loading
            try {
                delay(2000L)
                // ✨ 3. 더미 데이터를 새 데이터 클래스에 맞게 수정
                val dummyLocations = listOf(
                    LocationInfo("경기도 여주시 강천면", "관련주소: 가야리, 간매리, 걸은리, 굴암리, 도전리"),
                    LocationInfo("충남 논산시 강산동", "관련주소: 남교리, 대교리, 등화리, 부창리"),
                    LocationInfo("충남 논산시 강경읍", "관련주소: 남교리, 대흥리, 동홍리, 북옥리, 산양리"),
                    LocationInfo("경기도 포천시 창수면", "관련주소: 가양리, 고소성리, 신흥리, 오가리, 운산리"),
                    LocationInfo("경북 김천시 조마면", "관련주소: 강곡리, 대방리, 삼산리, 신곡리, 신안리")
                )
                _listUiState.value = LocationListUiState.Success(dummyLocations)
            } catch (e: Exception) {
                _listUiState.value = LocationListUiState.Error("목록을 불러오지 못했습니다.")
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _listUiState.value = LocationListUiState.Loading
            val hasPermission = ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                _listUiState.value = LocationListUiState.Error("위치 권한이 필요합니다.")
                return@launch
            }
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    Log.d(
                        "LocationVM",
                        "📍 GPS Location -> 위도: ${location.latitude}, 경도: ${location.longitude}"
                    )
                }
                delay(1500L)
                val newLocations = listOf(
                    LocationInfo("GPS 기반: 경기도 고양시", "관련주소: 일산동구, 일산서구, 덕양구"),
                    LocationInfo("GPS 기반: 서울특별시 은평구", "관련주소: 갈현동, 구산동, 녹번동, 불광동")
                )
                _listUiState.value = LocationListUiState.Success(newLocations)
            } catch (e: Exception) {
                _listUiState.value = LocationListUiState.Error("현재 위치를 가져올 수 없습니다: ${e.message}")
            }
        }
    }
}
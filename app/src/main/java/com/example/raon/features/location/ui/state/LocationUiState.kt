package com.example.raon.features.location.ui.state

sealed class LocationUiState {
    // 1. 로딩 중인 상태
    data object Loading : LocationUiState()

    // 2. 데이터를 성공적으로 가져온 상태 (데이터를 담고 있음)
    data class Success(val locations: List<String>) : LocationUiState()

    // 3. 에러가 발생한 상태 (에러 메시지를 담고 있음)
    data class Error(val message: String) : LocationUiState()
}
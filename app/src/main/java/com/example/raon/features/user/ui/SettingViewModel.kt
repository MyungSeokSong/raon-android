package com.example.raon.features.user.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.auth.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * 로그아웃을 수행하는 함수
     */
    fun logout() {
        viewModelScope.launch {
            // AuthRepository를 통해 토큰 삭제
            authRepository.logout()
        }
    }
}
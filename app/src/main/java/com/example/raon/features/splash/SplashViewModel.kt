package com.example.raon.features.splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.model.LoginState
import com.example.raon.features.auth.data.local.TokenManager
import com.example.raon.features.category.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager, // Hilt를 통해 TokenManager 주입
    private val categoryRepository: CategoryRepository // Hilt를 통해 CategoryRepository 주입
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState = _loginState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.setupCategoriesIfNeeded()
        }


        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            delay(1000) // 최소 1초간 스플래시 화면을 보여주기 위함 (선택사항)
            val accessToken = tokenManager.getAccessToken()
            if (accessToken.isNullOrEmpty()) {  // 토큰 없을 때
                _loginState.value = LoginState.LoggedOut
            } else {    // 토큰 있을 때
                _loginState.value = LoginState.LoggedIn
            }
        }
    }
}
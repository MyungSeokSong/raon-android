package com.example.raon.features.auth.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.auth.data.repository.AuthRepository
import com.example.raon.features.auth.ui.state.SignUpResult
import com.example.raon.features.auth.ui.state.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignUpResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val message: String) : LoginResult() // 로그인 성공
    data class Failure(val message: String) : LoginResult() // 로그인 실패
    data class ServerError(val message: String) : LoginResult() // 서버 에러

    class Error(val message: String) : LoginResult() // 예외상황 발생
}


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle // 👈 주입

) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()


    init {
        val address: String? = savedStateHandle["location"]
        val locationId: Int = savedStateHandle.get<Int>("locationId") ?: -1

        if (address != null && locationId != -1) {
            _uiState.update {
                it.copy(
                    userLocation = address,
                    userLocationId = locationId
                )
            }
        }
    }

    // --- UI 이벤트를 처리하는 함수들 ---
    fun onNicknameChange(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onPasswordCheckChange(passwordCheck: String) {
        _uiState.update { it.copy(passwordCheck = passwordCheck) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun togglePasswordCheckVisibility() {
        _uiState.update { it.copy(isPasswordCheckVisible = !it.isPasswordCheckVisible) }
    }


    fun signUp() {
        if (_uiState.value.signUpResult == SignUpResult.Loading) return

        // 1. 빈칸 검사 로직
        val currentState = _uiState.value
        if (currentState.nickname.isBlank() ||
            currentState.email.isBlank() ||
            currentState.password.isBlank() ||
            currentState.passwordCheck.isBlank() ||
            currentState.userLocation.isBlank() ||
            currentState.userLocationId == -1
        ) {
            _uiState.update {
                it.copy(signUpResult = SignUpResult.Failure("모든 항목을 입력해주세요."))
            }
            return
        }


        // 2. 비밀번호 일치 여부 검사
        if (currentState.password != currentState.passwordCheck) {
            _uiState.update {
                it.copy(signUpResult = SignUpResult.Failure("비밀번호가 일치하지 않습니다."))
            }
            return
        }

        // 3. 회원가입 절차 진행
        viewModelScope.launch {
            _uiState.update { it.copy(signUpResult = SignUpResult.Loading) }

            // 회원가입 repository 실행
            val resposen = authRepository.signup(
                currentState.nickname,
                currentState.email,
                currentState.password,
                locationId = currentState.userLocationId
            )

            try {
                delay(2000) // 가상 네트워크 딜레이
                _uiState.update { it.copy(signUpResult = SignUpResult.Success) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(signUpResult = SignUpResult.Failure("회원가입에 실패했습니다: ${e.message}"))
                }
            }
        }
    }


    // SignUpResult를 다시 초기화 시키는 코드
    fun resultConsumed() {
        _uiState.update { it.copy(signUpResult = SignUpResult.Idle) }
    }
}
package com.example.raon.features.auth.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.auth.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val message: String) : LoginResult() // 로그인 성공
    data class Faliure(val message: String) : LoginResult() // 로그인 실패
    data class ServerError(val message: String) : LoginResult() // 서버 에러

    class Error(val message: String) : LoginResult() // 예외상황 발생
}

// @Inject constructor 사용 => 즉 Hilt 사용해서 context를 직접 주입 하지 않아도 됨
@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val authRepository: AuthRepository = AuthRepository() // -> Hilt 사용전
    private val authRepository: AuthRepository

) : ViewModel() {

    // UI 상태 관리를 위한 변수들
    var email by mutableStateOf("test1@email.com")
        private set
    var password by mutableStateOf("test1234")
        private set
    var passwordVisible by mutableStateOf(false)
        private set

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult = _loginResult.asStateFlow()

    // 텍스트 필드 관련 함수들
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    // 로그인 관련 함수들
    fun login(email: String, password: String) {
        viewModelScope.launch {

            _loginResult.value = LoginResult.Loading
            delay(1000)

            Log.d("LoginTest", "로그인 ViewModel 시작")

            // 모든 네트워크 로직을 Repository에 위임하고, 결과만 받아서 UI 상태를 업데이트.
            val result = authRepository.login(email, password)
            _loginResult.value = result
            Log.d("LoginState", "로그인 상태 $result")
            Log.d("LoginTest", "로그인 ViewModel 시작2")
            
        }
    }
}
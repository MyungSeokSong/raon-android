package com.example.raon.core.model

// State-Driven UI 기반
// 로그인 상태 정의
sealed class LoginState {
    object Loading : LoginState()
    object LoggedIn : LoginState()
    object LoggedOut : LoginState()
}
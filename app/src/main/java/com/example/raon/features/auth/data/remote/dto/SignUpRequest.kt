package com.example.raon.features.auth.data.remote.dto

data class SignUpRequest(
    val nickname: String,
    val email: String,
    val password: String
)

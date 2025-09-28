package com.example.raon.features.auth.data.remote.dto

data class SignUpResponse(
    val success: Boolean,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

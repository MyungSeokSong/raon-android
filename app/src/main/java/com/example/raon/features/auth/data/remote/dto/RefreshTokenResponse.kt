package com.example.raon.features.auth.data.remote.dto

data class RefreshTokenResponse(

    val accessToken: String,
    val accessTokenExpirationTime: Long
)

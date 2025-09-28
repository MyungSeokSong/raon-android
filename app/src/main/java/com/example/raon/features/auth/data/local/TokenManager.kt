package com.example.raon.features.auth.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"           // SharedPreferences 파일 이름
    private const val ACCESS_TOKEN_KEY = "access_token"   // Access Token 키
    private const val REFRESH_TOKEN_KEY = "refresh_token" // Refresh Token 키

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("TokenManager", "EncryptedSharedPreferences 생성 실패", e)
            context.getSharedPreferences("${PREFS_NAME}_error", Context.MODE_PRIVATE)
        }
    }

    // Access Token 저장
    fun saveAccessToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(ACCESS_TOKEN_KEY, token).apply()
        Log.d("LoginTest", "Access Token 저장 코드 실행 됨")
    }

    // Access Token 가져오기
    fun getAccessToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(ACCESS_TOKEN_KEY, null)
    }

    // Refresh Token 저장
    fun saveRefreshToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(REFRESH_TOKEN_KEY, token).apply()
        Log.d("LoginTest", "Refresh Token 저장 코드 실행 됨")
    }

    // Refresh Token 가져오기
    fun getRefreshToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(REFRESH_TOKEN_KEY, null)
    }

    // 토큰 모두 삭제 (로그아웃 시)
    fun clearTokens(context: Context) {
        getEncryptedPrefs(context).edit()
            .remove(ACCESS_TOKEN_KEY)  // Access Token 삭제
            .remove(REFRESH_TOKEN_KEY) // Refresh Token 삭제
            .apply()
    }
}
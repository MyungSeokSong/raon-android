package com.example.raon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raon.features.auth.ui.KakaoAuthViewModel
import com.example.raon.features.bottom_navigation.b_test.ui.ViewModelTest
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val kakaoAuthViewModel: KakaoAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 키 해시값 가지기
        val keyHash = Utility.getKeyHash(this)
        Log.e("키 해시", keyHash.toString())

        // KakaoSdk 초기화
        KakaoSdk.init(this, BuildConfig.Kakao_native_App_Key)

        enableEdgeToEdge()
        setContent {

            // 테스트용 viewmodel
            val viewmodeltest: ViewModelTest = viewModel()

            AppNavigation(modifier = Modifier, kakaoAuthViewModel, this, viewmodeltest)


        }
    }
}



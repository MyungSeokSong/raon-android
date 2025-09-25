package com.example.raon.features.auth.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.raon.R


@Composable
fun AuthView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: KakaoAuthViewModel,
    context: Context
) {
    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()         // 부모의 크기에 맞추기
                .padding(paddingValues), // 알림바 크기 만큼 띄우기
            horizontalAlignment = Alignment.CenterHorizontally,  // 자식들 수평 가운데 정렬
            verticalArrangement = Arrangement.Center    // 자식을 수직 가운데 정렬
        ) {

            // Oauth 화면 이미지
            Image(
                painter = painterResource(R.drawable.oauth_image),
                contentDescription = "Oauth 화면 이미지"
            )

            // 라온 텍스트
            Text(
                "라온", fontSize = 30.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )


            // 카카오 로그인 버튼
            Button(
                onClick = {
                    viewModel.handleKakaoLogin(context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE500),
                    contentColor = Color.Black
                )
            ) {
                Row {
                    Image(
                        painter = painterResource(R.drawable.kakaotalk_sharing_btn_small),
                        contentDescription = "카카오 로그인"
                    )
                    Text("카카오 로그인")
                }
            }

            // 이메일 로그인 버튼
            Button(
                onClick = {
                    navController.navigate("main")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212121)
                )

            ) {

                Text("로그인")
            }

            OutlinedButton(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("회원가입")
            }
        }
    }
}



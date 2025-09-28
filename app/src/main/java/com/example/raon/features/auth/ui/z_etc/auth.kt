package com.example.raon.features.auth.ui.z_etc

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
                .padding(paddingValues)
                .padding(horizontal = 15.dp), // 알림바 크기 만큼 띄우기

            horizontalAlignment = Alignment.CenterHorizontally,  // 자식들 수평 가운데 정렬
            verticalArrangement = Arrangement.Center    // 자식을 수직 가운데 정렬
        ) {

            // Oauth 화면 이미지
            Image(
                painter = painterResource(R.drawable.raon_icon),
                contentDescription = "Oauth 화면 이미지"
            )

            // 라온 텍스트
            Text(
                "Raon", fontSize = 30.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 30.dp, top = 30.dp)
            )


            // 이메일 로그인 버튼
            Button(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212121)
                )

            ) {

                Text("로그인")
            }

            // 이메일 회원가입 버튼
            OutlinedButton(
                onClick = {
                    navController.navigate("signUp")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .height(48.dp)
            ) {
                Text("회원가입")
            }

            // 테스트용 홈 이동 버튼
            Button(
                onClick = {
                    navController.navigate("main")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212121)
                )

            ) {

                Text("HOME")
            }

            // 새 Auth 화면 테스트 버튼
            OutlinedButton(
                onClick = {
                    navController.navigate("newAuthScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .height(48.dp)
            ) {
                Text("New Auth Screen")
            }


            // OR줄 나누기
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // 상하 여백
                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
            ) {
                // 줄선
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
                Text(" OR ", modifier = Modifier.padding(horizontal = 5.dp))

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }





            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // 상하 여백
                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
            ) {


                // 카카오 로그인 버튼
                Button(
                    onClick = {
                        viewModel.handleKakaoLogin(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
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

            }
        }
    }
}



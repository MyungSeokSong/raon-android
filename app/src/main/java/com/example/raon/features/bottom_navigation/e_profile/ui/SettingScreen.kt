package com.example.raon.features.bottom_navigation.e_profile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isNotificationOn by remember { mutableStateOf(true) }
    // ## 1. 로그아웃 팝업의 표시 여부를 관리하는 상태 변수 추가 ##
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ## 2. showLogoutDialog가 true일 때 AlertDialog를 표시 ##
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false }, // 팝업 바깥이나 뒤로가기 클릭 시
            title = {
                Text(text = "로그아웃", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "정말 로그아웃하시겠어요?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 실제 로그아웃 로직 실행
                        // ...
                        showLogoutDialog = false // 로직 실행 후 팝업 닫기
                    },
                    shape = RoundedCornerShape(12.dp),
                    // 사진과 비슷한 주황색 버튼으로 스타일링
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF97316),
                        contentColor = Color.White
                    )
                ) {
                    Text("로그아웃")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false } // '닫기' 누르면 팝업 닫기
                ) {
                    Text("닫기")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // ... (다른 설정 메뉴들은 그대로)
            item { SettingsCategoryHeader("계정") }
            item { SettingsMenuItem(title = "프로필 수정", onClick = { /*...*/ }) }
            item { SettingsMenuItem(title = "계정 및 로그인 정보", onClick = { /*...*/ }) }
            item { SettingsCategoryHeader("알림") }
            item {
                SettingsToggleItem(
                    title = "채팅 및 활동 알림",
                    checked = isNotificationOn,
                    onCheckedChange = { isNotificationOn = it }
                )
            }
            item { SettingsCategoryHeader("정보") }
            item { SettingsMenuItem(title = "공지사항", onClick = { /*...*/ }) }
            item { SettingsMenuItem(title = "서비스 이용약관", onClick = { /*...*/ }) }
            item { SettingsMenuItem(title = "개인정보 처리방침", onClick = { /*...*/ }) }
            item { SettingsInfoItem(title = "앱 버전", value = "1.0.0") }

            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Divider()
                    Text(
                        text = "로그아웃",
                        modifier = Modifier
                            .fillMaxWidth()
                            // ## 3. '로그아웃' 텍스트 클릭 시 상태 변수를 true로 변경 ##
                            .clickable { showLogoutDialog = true }
                            .padding(16.dp)
                    )
                    Divider()
                    Text(
                        text = "회원탈퇴",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: 회원탈퇴 팝업 표시 */ }
                            .padding(16.dp)
                    )
                    Divider()
                }
            }
        }
    }
}


// ... (아래 재사용 컴포저블들은 기존과 동일)
@Composable
private fun SettingsCategoryHeader(title: String) { /*...*/
}

@Composable
private fun SettingsMenuItem(title: String, onClick: () -> Unit) { /*...*/
}

@Composable
private fun SettingsToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) { /*...*/
}

@Composable
private fun SettingsInfoItem(title: String, value: String) { /*...*/
}
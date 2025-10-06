package com.example.raon.features.user.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // 알림 설정 On/Off 상태를 관리하기 위한 변수
    var isNotificationOn by remember { mutableStateOf(true) }
    // 로그아웃 팝업의 표시 여부를 관리하는 상태 변수
    var showLogoutDialog by remember { mutableStateOf(false) }

    // showLogoutDialog가 true일 때 AlertDialog를 표시
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

                        viewModel.logout()  // 로그아웃
                        
                        onLogout()  // 로그아웃시 화면 이동 함수
                        showLogoutDialog = false // 로직 실행 후 팝업 닫기
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF97316), // 주황색 버튼
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
        // 설정 항목이 많아질 수 있으므로 LazyColumn을 사용
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 계정 카테고리
            item { SettingsCategoryHeader("계정") }
            item {
                SettingsMenuItem(title = "프로필 수정", onClick = { /* TODO: 프로필 수정 화면으로 이동 */ })
            }
            item {
                SettingsMenuItem(title = "계정 및 로그인 정보", onClick = { /* TODO: 계정 정보 화면으로 이동 */ })
            }

            // 알림 카테고리
            item { SettingsCategoryHeader("알림") }
            item {
                // 스위치를 포함한 메뉴 아이템
                SettingsToggleItem(
                    title = "채팅 및 활동 알림",
                    checked = isNotificationOn,
                    onCheckedChange = { isNotificationOn = it }
                )
            }

            // 정보 카테고리
            item { SettingsCategoryHeader("정보") }
            item {
                SettingsMenuItem(title = "공지사항", onClick = { /* TODO: 공지사항 화면으로 이동 */ })
            }
            item {
                SettingsMenuItem(title = "서비스 이용약관", onClick = { /* TODO: 웹뷰 또는 화면 이동 */ })
            }
            item {
                SettingsMenuItem(title = "개인정보 처리방침", onClick = { /* TODO: 웹뷰 또는 화면 이동 */ })
            }
            item {
                // 버전 정보처럼 클릭이 필요 없는 메뉴 아이템
                SettingsInfoItem(title = "앱 버전", value = "1.0.0")
            }

            // 로그아웃 및 회원탈퇴
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    HorizontalDivider()
                    Text(
                        text = "로그아웃",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true } // 클릭 시 팝업 상태를 true로 변경
                            .padding(16.dp)
                    )
                    HorizontalDivider()
                    Text(
                        text = "회원탈퇴",
                        color = MaterialTheme.colorScheme.error, // 위험한 작업임을 알리는 색상
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: 회원탈퇴 확인 팝업 표시 */ }
                            .padding(16.dp)
                    )
                    HorizontalDivider()

                }
            }
        }
    }
}

// 재사용을 위한 컴포저블들

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsMenuItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsInfoItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Text(text = value, color = Color.Gray)
    }
}
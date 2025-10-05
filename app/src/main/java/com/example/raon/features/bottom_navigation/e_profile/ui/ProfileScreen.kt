package com.example.raon.features.bottom_navigation.e_profile.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage

// 앱의 시그니처 색상을 상수로 정의
private val BrandYellow = Color(0xFFFDCC31)
private val DarkGrayText = Color(0xFF3C3C3C)

/**
 * TopAppBar: 설정 아이콘 포함
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("프로필") },
        actions = {
            IconButton(onClick = { navController.navigate("settings_screen") }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정으로 이동"
                )
            }
        }
    )
}

/**
 * ProfileScreen 전체 UI
 */
@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader_NoData()
        Divider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        ProfileMenuList(navController = navController)
    }
}

/**
 * 프로필 상단 헤더 UI
 */
@Composable
fun ProfileHeader_NoData() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://picsum.photos/seed/myprofile/300/300",
            contentDescription = "프로필 사진",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, BrandYellow, CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "멋진 개발자",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "서초동",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: 프로필 수정 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandYellow,
                contentColor = DarkGrayText
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(text = "프로필 수정", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * 프로필 메뉴 리스트 UI
 */
@Composable
fun ProfileMenuList(navController: NavController) {
    Column {
        Text(
            text = "나의 거래",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            // ## 변경: 소제목 색상을 설정 페이지와 동일한 테마 기본 색상으로 ##
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        MenuRow(icon = Icons.Filled.ShoppingCart, title = "구매내역", onClick = { /*TODO*/ })
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        MenuRow(
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            title = "판매내역",
            onClick = { /*TODO*/ })
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        MenuRow(icon = Icons.Filled.FavoriteBorder, title = "관심목록", onClick = { /*TODO*/ })
        Divider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        Text(
            text = "기타",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            // ## 변경: 소제목 색상을 설정 페이지와 동일한 테마 기본 색상으로 ##
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        MenuRow(
            icon = Icons.Filled.Settings,
            title = "앱 설정",
            onClick = { navController.navigate("settings_screen") }
        )
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        MenuRow(icon = Icons.Filled.Info, title = "공지사항", onClick = { /*TODO*/ })
    }
}

/**
 * 메뉴 리스트의 각 행 UI
 */
@Composable
fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = DarkGrayText
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "이동",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}
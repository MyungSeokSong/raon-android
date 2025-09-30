package com.example.raon.features.location.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LocationSetupScreen(modifier: Modifier = Modifier) {
    FindingLocationScreenPreview()
}


/**
 * 당근마켓 스타일의 위치 탐색 UI
 * 앱이 현재 위치를 찾는 동안 보여주는 화면입니다.
 */
@Composable
fun FindingLocationScreen(
    onSetCurrentLocationClick: () -> Unit = {},
    onSearchManuallyClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 컨텐츠 (로딩 아이콘과 텍스트)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "현재 위치를\n찾고 있어요",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "정확한 동네 설정을 위해\n잠시만 기다려주세요.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            // 하단 버튼
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = onSetCurrentLocationClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("현재 위치로 설정", fontSize = 18.sp)
                }
                TextButton(onClick = onSearchManuallyClick) {
                    Text("내 동네 이름으로 검색", color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Finding Location UI Preview")
@Composable
fun FindingLocationScreenPreview() {
    MaterialTheme {
        FindingLocationScreen()
    }
}


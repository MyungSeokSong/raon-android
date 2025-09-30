package com.example.raon.features.location.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen() {
    var searchText by remember { mutableStateOf("") }

    val nearbyLocations = listOf(
        "서울 영등포구 문래동6가", "서울 영등포구 문래동5가", "서울 영등포구 문래동4가",
        "서울 영등포구 양평동1가", "서울 영등포구 양평제1동", "서울 영등포구 양평동2가",
        "서울 영등포구 당산동2가", "서울 영등포구 문래동3가", "서울 영등포구 문래동2가",
        "서울 영등포구 당산동1가", "서울 영등포구 문래동", "서울 영등포구 신길동",
        "서울 영등포구 도림동"
    )

    // 👇 1. Scaffold로 전체 UI를 감싸줍니다.
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 👇 2. Scaffold가 제공하는 paddingValues를 적용합니다.
                .padding(paddingValues)
                .padding(16.dp) // 기존의 16dp 패딩은 유지합니다.
        ) {
            // 1. 검색창
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("내 동네 이름(동,읍,면)으로 검색") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 현재 위치로 찾기 버튼
            Button(
                onClick = { /* TODO: 현재 위치 찾기 로직 실행 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDCC31),
                    contentColor = Color(0xFF3C3C3C)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.MyLocation,
                        contentDescription = "Current Location Icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("현재 위치로 찾기", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. '근처 동네' 섹션
            Text("근처 동네", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // 4. 근처 동네 목록
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(nearbyLocations) { location ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: 선택된 동네 처리 로직 */ }
                    ) {
                        Text(
                            text = location,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Location Search UI Preview")
@Composable
fun LocationSearchScreenPreview() {
    MaterialTheme {
        LocationSearchScreen()
    }
}
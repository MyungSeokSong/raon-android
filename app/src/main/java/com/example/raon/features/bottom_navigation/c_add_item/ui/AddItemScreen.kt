package com.example.raon.features.bottom_navigation.c_add_item.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raon.R


@Composable
fun AddItemScreen(modifier: Modifier = Modifier) {

    var text by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp),

        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()    // 이 코드를 추가하니깐 topbar와 시스템 알림바가 안 겹침

                    .fillMaxWidth()
                    .heightIn(56.dp) // 표준 TopAppBar 높이 (Material Design 가이드라인)
//                    .background(MaterialTheme.colorScheme.primary) // Material Theme의 primary 색상 사용
                    .padding(horizontal = 4.dp), // 좌우 패딩
                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
            ) {


                // 왼쪽 오른쪽 공간 비율을 맞춰서 텍스트를 중앙에 정렬하기 위함
                IconButton(
                    onClick = {},
                    modifier = Modifier.alpha(0f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기" // 접근성을 위한 설명
                    )
                }
                Text(
                    text = "판매 등록",
//                    color = MaterialTheme {  }.colorScheme.onPrimary, // primary 색상 위 텍스트 색상
                    fontSize = 20.sp, // 적절한 글자 크기
                    modifier = Modifier.weight(1f), // 제목을 중앙에 가깝게 하려면 이 라인을 사용할 수 있음
                    textAlign = TextAlign.Center

                )

                // 창을 종료하는 X 아이콘
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기" // 접근성을 위한 설명
                    )
                }

            }
        },
        bottomBar = {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .navigationBarsPadding()
            ) {
                Text("등록 완료")
            }
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
//            .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                "상품설명", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(BorderStroke(1.dp, Color.Gray)),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = "카메라 버튼 아이콘"
                    )
                    Text("0/5")
                }
            }


            Text(
                "제목", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 5.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                placeholder = { Text("제목") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Text(
                "설명", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier.padding(top = 30.dp, bottom = 5.dp)
            )

            OutlinedTextField(
                value = text2,
                onValueChange = {
                    text2 = it
                },
                placeholder = { Text("설명") },
                modifier = Modifier
                    .fillMaxWidth(),
                minLines = 7

            )

            Text(
                "가격", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier.padding(top = 30.dp, bottom = 5.dp)
            )

            OutlinedTextField(
                value = text2,
                onValueChange = {
                    text2 = it
                },
                placeholder = { Text("₩ 가격") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


        }


    }
}


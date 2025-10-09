package com.example.raon.features.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.raon.R


// 채팅 입력 UI
@Composable
fun MessageInput(modifier: Modifier = Modifier) {

    var chatMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {

        // 채팅바 구성
        // 1. 갤러리 아이콘, 2. 채팅 입력 부분, 3. 전송버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 1. 갤러리 아이콘
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = "채팅 플러스 아이콘",
                    modifier = Modifier.size(36.dp)
                )
            }

            // 2. 채팅 메시지 입력 필드
            BasicTextField(
                value = chatMessage,
                onValueChange = { it ->
                    chatMessage = it
                },
                modifier = Modifier
                    .weight(1f),

                // BasicTextField의 핵심 부분 -> decorationBox로 커스터 마이징을 한다
                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEEEEE), RoundedCornerShape(30.dp))
                            .padding(horizontal = 10.dp, vertical = 10.dp)

                    ) {
                        if (chatMessage.isEmpty()) {
                            Text(
                                text = "메시지 입력",
                                color = Color.LightGray

                            )
                        }
                        innerTextField()
                    }
                }
            )


            // 3. 채팅 보내기 아이콘
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "채팅 보내는 아이콘"
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MessageInputPreview(modifier: Modifier = Modifier) {
    MessageInput()
}
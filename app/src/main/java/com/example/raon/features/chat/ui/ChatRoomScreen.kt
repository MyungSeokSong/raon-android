package com.example.raon.features.chat.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raon.R
import com.example.raon.features.chat.domain.model.ChatMessage

val BrandYellow = Color(0xFFFDCC31)
val DarkGrayText = Color(0xFF3C3C3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    onBackClick: (chatId: Long) -> Unit,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    // 👇 ViewModel의 단일 상태(uiState)만 구독합니다.
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // ❗️ `showWarningBanner`와 `warningMessage`를 따로 구독할 필요가 없어졌습니다.

    LaunchedEffect(uiState.messages) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("쫀딕", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            val idToSend = viewModel.chatRoomId
                            // 👇 [로그 추가] 뒤로가기 버튼 누르는 순간의 ID 확인
                            Log.d(
                                "ChatReadDebug",
                                "1. Back button clicked in ChatRoomScreen. Sending chatId: $idToSend"
                            )
                            onBackClick(idToSend)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = DarkGrayText,
                        navigationIconContentColor = DarkGrayText
                    )
                )
                ProductInfoBar(
                    productImageUrl = "https://via.placeholder.com/150",
                    productStatus = "판매중",
                    productName = "미닉스 건조기",
                    productPrice = "135,000원"
                )
                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            }
        },
        bottomBar = {
            MessageInput(onSendClick = { text -> viewModel.sendMessage(text) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF2F2F7))
        ) {
            Button(
                onClick = { viewModel.detectFraud() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("⚠️ 사기 탐지 API 테스트 버튼")
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(uiState.messages.reversed()) { message ->
                    MessageBubble(message = message)
                }
            }

            // 👇 경고 배너 표시 여부와 메시지를 모두 uiState에서 가져옵니다.
            AnimatedVisibility(visible = uiState.fraudWarningMessage != null) {
                FraudWarningBanner(
                    message = uiState.fraudWarningMessage.orEmpty(),
                    onClose = { viewModel.closeWarningBanner() }
                )
            }
        }
    }
}

// (ProductInfoBar, MessageBubble, MessageInput, FraudWarningBanner 함수는 변경사항 없음)
@Composable
fun ProductInfoBar(
    productImageUrl: String,
    productStatus: String,
    productName: String,
    productPrice: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Product Image",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$productStatus $productName",
                color = DarkGrayText,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = productPrice,
                color = DarkGrayText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun MessageBubble(message: ChatMessage) {
    val horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromMe) {
            Spacer(modifier = Modifier.width(8.dp))
        }

        val bubbleColor = if (message.isFromMe) Color(0xFFF9A825) else Color.White
        val textColor = if (message.isFromMe) Color.White else DarkGrayText

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = bubbleColor,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MessageInput(onSendClick: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 0.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* 첨부파일 기능 */ }) {
                Icon(Icons.Default.Add, contentDescription = "Attach File", tint = Color.Gray)
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지 보내기", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 4
            )

            if (text.isBlank()) {
                IconButton(onClick = { /* 이모티콘 창 열기 */ }) {
                    Icon(
                        Icons.Default.SentimentSatisfied,
                        contentDescription = "Emoji",
                        tint = Color.Gray
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onSendClick(text)
                        text = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = BrandYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun FraudWarningBanner(
    message: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "경고",
            tint = Color(0xFFE65100)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            color = Color(0xFF4E342E),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "경고 닫기",
                tint = Color.Gray
            )
        }
    }
}
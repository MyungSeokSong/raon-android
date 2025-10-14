package com.example.raon.features.chat.ui

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
import androidx.compose.material.icons.filled.SentimentSatisfied
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

// 요청하신 색상을 정의합니다.
val BrandYellow = Color(0xFFFDCC31)
val DarkGrayText = Color(0xFF3C3C3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            // ✨ 변경점: TopAppBar와 ProductInfoBar를 함께 표시하기 위해 Column으로 감싸기
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("쫀딕", fontWeight = FontWeight.Bold) }, // 타이틀을 중앙에 배치
                    navigationIcon = {
                        IconButton(onClick = { /* 뒤로가기 동작 */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = DarkGrayText,
                        navigationIconContentColor = DarkGrayText
                    )
                )
                // ✨ 여기에 상품 정보 바를 추가합니다.
                ProductInfoBar(
                    // TODO: 실제 상품 이미지, 정보로 변경해야 합니다.
                    productImageUrl = "https://via.placeholder.com/150", // 예시 URL
                    productStatus = "거래완료",
                    productName = "미닉스 건조기",
                    productPrice = "135,000원"
                )
                // 상품 정보 바와 채팅 내역을 구분하는 선
                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            }
        },
        bottomBar = {
            MessageInput(onSendClick = { text -> viewModel.sendMessage(text) })
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF2F2F7)), // 이미지와 유사한 배경색으로 변경
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            items(uiState.messages.reversed()) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

// ✨ 새로 추가된 상품 정보 바 Composable
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
        // 상품 이미지 (Coil 라이브러리 사용 권장)
        Image(
            // painter = painterResource(id = R.drawable.product_image), // 로컬 이미지 사용 시
            painter = painterResource(id = R.drawable.ic_launcher_background), // 예시 이미지
            contentDescription = "Product Image",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))

        // 상품 정보
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
            // Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(40.dp).clip(CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
        }

        val bubbleColor = if (message.isFromMe) Color(0xFFF9A825) else Color.White // 오렌지색, 흰색 말풍선
        val textColor = if (message.isFromMe) Color.White else DarkGrayText

        Surface(
            shape = RoundedCornerShape(18.dp), // 양쪽 말풍선 모양 통일
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
        shadowElevation = 0.dp, // 그림자 제거
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 '+' 아이콘
            IconButton(onClick = { /* 첨부파일 기능 */ }) {
                Icon(Icons.Default.Add, contentDescription = "Attach File", tint = Color.Gray)
            }

            // 메시지 입력 필드
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

            // 이모티콘 및 보내기 버튼
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
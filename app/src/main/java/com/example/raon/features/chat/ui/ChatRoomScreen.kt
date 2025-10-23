package com.example.raon.features.chat.ui

// import androidx.compose.material.icons.filled.ImageSearch // [삭제]
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.raon.features.chat.domain.model.ChatMessage
import com.example.raon.ui.theme.BrandDarkText
import com.example.raon.ui.theme.BrandYellow
import com.example.raon.ui.theme.ChatBackgroundColor
import com.example.raon.ui.theme.DarkGrayText
import com.example.raon.ui.theme.OtherBubbleColor
import com.example.raon.ui.theme.OtherTextColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    onBackClick: (chatId: Long) -> Unit,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    // ViewModel의 단일 상태(uiState)만 구독합니다.
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()


    LaunchedEffect(uiState.messages) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            uiState.opponentNickname ?: "",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            val idToSend = viewModel.chatRoomId
                            // [로그 추가] 뒤로가기 버튼 누르는 순간의 ID 확인
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
                // [수정] ProductInfoBar에 isBuyer 플래그와 onAIImageAnalyzeClick 람다 전달
                ProductInfoBar(
                    productImageUrl = uiState.productInfo?.viewableThumbnailUrl
                        ?: "", // Presigned URL
                    productStatus = uiState.productInfo?.status ?: "",
                    productName = uiState.productInfo?.productName ?: "",
                    productPrice = uiState.productInfo?.price?.let { "%,d원".format(it) }
                        ?: "",
                    isBuyer = uiState.isCurrentUserBuyer, // ViewModel의 상태 전달
                    onAIImageAnalyzeClick = {
                        viewModel.analyzeImage() // ViewModel의 새 함수 호출
                    }
                )



                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            }
        },
        bottomBar = {
            // [수정] MessageInput에도 isBuyer 상태를 전달합니다.
            MessageInput(
                onSendClick = { text -> viewModel.sendMessage(text) },
                onAIDetectFraud = { viewModel.detectFraud() },
                isBuyer = uiState.isCurrentUserBuyer // [추가]
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ChatBackgroundColor)
        ) {

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

            // 경고 배너 표시 여부와 메시지를 모두 uiState에서 가져옵니다.
            AnimatedVisibility(visible = uiState.fraudWarningMessage != null) {
                FraudWarningBanner(
                    message = uiState.fraudWarningMessage.orEmpty(),
                    onClose = { viewModel.closeWarningBanner() }
                )
            }
        }
    }
}

// [수정] ProductInfoBar (텍스트 버튼 디자인으로 변경 및 우측 정렬, UI/크기 개선)
@Composable
fun ProductInfoBar(
    productImageUrl: String,
    productStatus: String,
    productName: String,
    productPrice: String,
    isBuyer: Boolean, // 구매자인지 여부
    onAIImageAnalyzeClick: () -> Unit // 버튼 클릭 람다
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬 유지
    ) {
        AsyncImage(
            model = productImageUrl, // 전달받은 Presigned URL 사용
            contentDescription = "Product Image",
            modifier = Modifier
                .size(48.dp) // 이미지 크기
                .clip(RoundedCornerShape(8.dp)) // 모서리 둥글게
                .background(Color.LightGray), // Placeholder 배경색
            contentScale = ContentScale.Crop // 비율 유지하며 채우기
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f), // 텍스트 영역이 남은 공간을 모두 차지 (버튼을 밀어냄)
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$productStatus  $productName",
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

        // [수정] AI 사진 분석하기 버튼 UI 및 크기 개선
        if (isBuyer) {
            Spacer(modifier = Modifier.width(12.dp)) // 텍스트와 버튼 사이 간격
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE0E0E0), // 살짝 더 진한 회색 배경
                        shape = RoundedCornerShape(20.dp) // 모서리를 더 둥글게 (알약 형태)
                    )
                    .clip(RoundedCornerShape(20.dp)) // clickable 영역을 background와 일치
                    .clickable { onAIImageAnalyzeClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp), // [수정] 내부 패딩 증가
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome, // AI 아이콘 유지
                    contentDescription = "AI 분석",
                    tint = DarkGrayText, // 아이콘 색상을 DarkGrayText로
                    modifier = Modifier.size(18.dp) // [수정] 아이콘 크기 증가
                )
                Spacer(modifier = Modifier.width(5.dp)) // [수정] 아이콘과 텍스트 사이 간격
                Text(
                    text = "AI 사진 분석하기",
                    fontSize = 13.sp, // [수정] 폰트 크기 증가
                    fontWeight = FontWeight.Medium,
                    color = DarkGrayText // 텍스트 색상
                )
            }
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

        val bubbleColor = if (message.isFromMe) BrandYellow else OtherBubbleColor
        val textColor = if (message.isFromMe) BrandDarkText else OtherTextColor

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


// [수정] MessageInput 함수 시그니처에 isBuyer 파라미터 추가
@Composable
fun MessageInput(
    onSendClick: (String) -> Unit,
    onAIDetectFraud: () -> Unit,
    isBuyer: Boolean // [추가]
) {
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

            // [수정] 구매자인 경우(isBuyer == true)에만 AI (텍스트) 분석 버튼 표시
            if (isBuyer) {
                IconButton(onClick = {
                    onAIDetectFraud()
                }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome, // AI 아이콘
                        contentDescription = "AI 분석",
                        tint = Color.Gray // 다른 아이콘과 톤을 맞춤
                    )
                }
            }

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
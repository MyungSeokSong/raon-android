package com.example.raon.features.chat.ui

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
import com.example.raon.R
import com.example.raon.features.chat.domain.model.ChatMessage

// ❗️❗️❗️ 요청하신 대로 BrandYellow2와 DarkGrayText2 변수명만 사용하도록 수정합니다. ❗️❗️❗️
val BrandYellow2 = Color(0xFFFDCC31)
val DarkGrayText2 = Color(0xFF3C3C3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen2(
    // onBackClick: () -> Unit,
    // viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()

    var showWarningBanner by remember { mutableStateOf(true) }
    val sampleWarningMessage = "[상대]가 택배비를 요구하며 즉시 입금을 요청하고 있습니다. 앱 외부 거래는 사기 위험이 높으니 주의하세요."
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("쫀딕", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { /* 뒤로가기 동작 */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = DarkGrayText2,
                        navigationIconContentColor = DarkGrayText2
                    )
                )
                ProductInfoBar2(
                    productImageUrl = "https://via.placeholder.com/150",
                    productStatus = "판매중",
                    productName = "미닉스 건조기",
                    productPrice = "135,000원"
                )
                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            }
        },
        bottomBar = {
            MessageInput(
                text = text,
                onTextChange = { newText -> text = newText },
                onSendClick = {
                    text = ""
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF2F2F7))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                val sampleMessages = listOf(
                    ChatMessage(
                        messageId = 4,
                        chatRoomId = 1,
                        senderId = 1001,
                        senderNickname = "나",
                        senderProfileUrl = null,
                        content = "아... 네 알겠습니다.",
                        imageUrl = null,
                        timestamp = "10:04 AM",
                        isFromMe = true
                    ),
                    ChatMessage(
                        messageId = 3,
                        chatRoomId = 1,
                        senderId = 2002,
                        senderNickname = "쫀딕",
                        senderProfileUrl = null,
                        content = "네 그럼요. 근데 카톡으로 연락주실 수 있나요? 아이디는 safe-trade 입니다.",
                        imageUrl = null,
                        timestamp = "10:03 AM",
                        isFromMe = false
                    ),
                    ChatMessage(
                        messageId = 2,
                        chatRoomId = 1,
                        senderId = 1001,
                        senderNickname = "나",
                        senderProfileUrl = null,
                        content = "네 안녕하세요! 혹시 이 제품 아직 판매하시나요?",
                        imageUrl = null,
                        timestamp = "10:02 AM",
                        isFromMe = true
                    ),
                    ChatMessage(
                        messageId = 1,
                        chatRoomId = 1,
                        senderId = 2002,
                        senderNickname = "쫀딕",
                        senderProfileUrl = null,
                        content = "안녕하세요!",
                        imageUrl = null,
                        timestamp = "10:01 AM",
                        isFromMe = false
                    )
                )
                items(sampleMessages) { message ->
                    MessageBubble2(message = message)
                }
            }

            AnimatedVisibility(visible = showWarningBanner) {
                FraudWarningBanner(
                    message = sampleWarningMessage,
                    onClose = { showWarningBanner = false }
                )
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

@Composable
fun ProductInfoBar2(
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
                color = DarkGrayText2,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = productPrice,
                color = DarkGrayText2,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MessageBubble2(message: ChatMessage) {
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
        val textColor = if (message.isFromMe) Color.White else DarkGrayText2

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
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
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
                onValueChange = onTextChange,
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
                IconButton(onClick = onSendClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = BrandYellow2
                    )
                }
            }
        }
    }
}
package com.example.raon.features.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.raon.features.chat.data.remote.dto.ChatRoomInfo
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ChatListScreen(
    onChatRoomClick: (chatRoomId: Long, sellerId: Long) -> Unit,
    chatRooms: List<ChatRoomInfo>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = chatRooms,
            key = { it.chatId } // 각 아이템의 고유 키는 chatId로 설정
        ) { chatRoom ->
            ChatListItem(
                chatRoom = chatRoom,
                onClick = {

                    // 채티방 터치 이벤트 발생시 실행하는 함수 -> chatroomId, sellerId 전달
                    onChatRoomClick(chatRoom.chatId, chatRoom.seller.userId)

                    // 아이템 클릭 시 해당 chatId를 가지고 채팅방 화면으로 이동
//                    navController.navigate("chatRoom/${chatRoom.chatId}")

                    Log.d("채팅프로세스1", "ChatListScreen ->  채팅방 ID: ${chatRoom.chatId}")
                }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

@Composable
private fun ChatListItem(
    chatRoom: ChatRoomInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 상대방 프로필 이미지 (또는 상품 썸네일)
        AsyncImage(
            model = chatRoom.buyer.profileImage ?: chatRoom.product.thumbnail,
            contentDescription = "채팅 상대 프로필 이미지",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chatRoom.seller.nickname,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTimeAgo(chatRoom.lastMessage?.sentAt),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 마지막 채팅 내용
            Text(
                text = chatRoom.lastMessage?.content ?: "대화 내용이 없습니다.",
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 안 읽은 메시지 개수 (있을 경우에만 표시)
        if (chatRoom.unreadCount > 0) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = chatRoom.unreadCount.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

// 시간 포맷팅을 위한 헬퍼 함수
@Composable
private fun formatTimeAgo(dateTimeString: String?): String {
    if (dateTimeString == null) return ""
    return try {
        // Z(UTC) 정보가 없는 시간이므로 "T"를 추가하고 임의의 Z를 붙여 OffsetDateTime으로 파싱
        val createdTime = OffsetDateTime.parse(dateTimeString.replace(" ", "T") + "Z")
        val now = OffsetDateTime.now()

        val minutes = ChronoUnit.MINUTES.between(createdTime, now)
        val hours = ChronoUnit.HOURS.between(createdTime, now)
        val days = ChronoUnit.DAYS.between(createdTime, now)

        when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days < 7 -> "${days}일 전"
            else -> "${createdTime.monthValue}월 ${createdTime.dayOfMonth}일"
        }
    } catch (e: Exception) {
        "시간 정보 없음"
    }
}
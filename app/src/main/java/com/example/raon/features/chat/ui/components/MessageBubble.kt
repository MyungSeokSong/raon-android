package com.example.raon.features.chat.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.raon.R


// 채팅 말풍선 UI
@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    message: String,        // 메세지 데이터
    isMyMessage: Boolean    // true면 내 채팅, false면 다른 사람 채팅
) {

    // 1. 채팅 주인 판별
    // 2. 텍스트인지 이미지인지
    // 3. 말풍선 색

    val alignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor =
        if (isMyMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        contentAlignment = alignment
    ) {

        // 내가 아닐 경우 채팅 프로필 이미지 추가
        if (!isMyMessage) {
            Row {

                val imageUrl =
                    "data:image/jpeg;base64,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"
                AsyncImage(
                    model = if (LocalInspectionMode.current) {
                        R.drawable.profile_image_cat
                    } else {
                        imageUrl
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )



                MessageBubbleContent(message, backgroundColor)

            }
        }
        // 나일 경우 메시지만 보여주기
        else {
            MessageBubbleContent(message, backgroundColor)
        }


    }
}

// 채팅 메시지 내용 UI
@Composable
fun MessageBubbleContent(message: String, backgroundColor: Color) {

    Surface(
        shape = RoundedCornerShape(15.dp),
        color = backgroundColor,
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = 7.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier
                .padding(10.dp)
        )

    }

}


// UI 미리 보기
@Preview(showSystemUi = true)
//@Preview
@Composable
fun MessageBubblePreview(modifier: Modifier = Modifier) {

    Column {
        MessageBubble(modifier, "hi", false)
        MessageBubble(modifier, "hi", false)
        MessageBubble(modifier, "hi", true)
    }

}
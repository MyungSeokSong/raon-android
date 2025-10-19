package com.example.raon.core.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.raon.core.ui.model.ItemListUiModel

/**
 * 여러 화면에서 재사용될 아이템 목록 UI
 * @param items 표시할 아이템 데이터 리스트 (공통 모델 사용)
 * @param onItemClick 아이템 클릭 시 호출될 콜백 (클릭된 아이템의 id 전달)
 * @param isFavoriteList 현재 이 리스트가 찜 목록인지를 나타냅니다. (하트 아이콘 표시 여부 결정)
 * @param onFavoriteClick 하트 아이콘 클릭 시 호출될 콜백 (클릭된 아이템의 id 전달)
 */
@Composable
fun ItemListComponoents(
    items: List<ItemListUiModel>,
    onItemClick: (Int) -> Unit,
    isFavoriteList: Boolean = false,
    onFavoriteClick: (itemId: Int) -> Unit = {}
) {
    LazyColumn {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            ItemListItem(
                item = item,
                onClick = { onItemClick(item.id) },
                isFavoriteItem = isFavoriteList,
                onFavoriteClick = { onFavoriteClick(item.id) }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

/**
 * 아이템 목록의 개별 항목(Row) UI
 */
@Composable
private fun ItemListItem(
    item: ItemListUiModel,
    onClick: () -> Unit,
    isFavoriteItem: Boolean,
    onFavoriteClick: () -> Unit
) {
    val lastLocation = item.location.split(" ").lastOrNull() ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .height(100.dp)
                .weight(1f)
        ) {
            // 제목, 위치/시간, 가격
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$lastLocation · ${item.timeAgo}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (item.price > 0) {
                    Text(
                        text = "%,d원".format(item.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }

            // 조회수, 댓글, 좋아요 표시
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.viewCount > 0) {
                    Icon(
                        imageVector = Icons.Outlined.RemoveRedEye,
                        contentDescription = "조회수",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = item.viewCount.toString(), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (item.comments > 0) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "댓글",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = item.comments.toString(), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (item.likes > 0) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "좋아요",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = item.likes.toString(), fontSize = 13.sp, color = Color.Gray)
                }
            }

            // 찜 목록인 경우에만 하트 아이콘 표시
            if (isFavoriteItem) {
                // isFavorite 상태에 따라 아이콘과 색상을 결정
                val icon =
                    if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                val tint = if (item.isFavorite) Color.Red else Color.Gray

                Icon(
                    imageVector = icon,
                    contentDescription = "관심 상품 토글",
                    tint = tint,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .padding(top = 4.dp, end = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            // 클릭 시 ViewModel에 알림
                            onFavoriteClick()
                        }
                )
            }
        }
    }
}
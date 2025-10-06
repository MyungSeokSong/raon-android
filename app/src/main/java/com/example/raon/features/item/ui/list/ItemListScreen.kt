package com.example.raon.features.item.ui.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.raon.features.item.ui.list.model.ItemUiModel

@Composable
fun ItemListScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemListViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit,
    onNavigateToWritePost: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        
        // uiState.items 상테로 바꾸기 -> itemList를 가져와서 UI로 보여줌
        ItemList(items = uiState.items) // 이름 및 파라미터 변경


        WritePostFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = onNavigateToWritePost
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

// --- 이하 부속 Composable 함수들 ---

@Composable
fun HomeScreenTopAppBar(
    onNavigateToSearch: () -> Unit
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "대자동", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "지역 선택")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row {
                IconButton(onClick = onNavigateToSearch) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
                IconButton(onClick = { /* TODO: 알림 화면으로 이동 */ }) {
                    Icon(Icons.Outlined.NotificationsNone, contentDescription = "알림")
                }
            }
        }
    }
}

@Composable
fun ItemList(items: List<ItemUiModel>) { // 이름 및 파라미터 변경
    LazyColumn {
        items(
            items = items, // 파라미터 사용
            key = { it.id }
        ) { item -> // 변수명 변경
            ItemListItem(item = item) // 이름 변경
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

@Composable
fun ItemListItem(item: ItemUiModel) { // 이름 및 파라미터 타입 변경
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
        Box(modifier = Modifier.height(100.dp)) {
            // 제목, 위치/시간, 가격
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(
                        onClick = { /* TODO: 더보기 메뉴 표시 */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.location} · ${item.timeAgo}",
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
            // 댓글/좋아요
            if (item.comments > 0 || item.likes > 0) {
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            }
        }
    }
}

@Composable
fun WritePostFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFF76707),
        contentColor = Color.White,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "글쓰기")
            Spacer(modifier = Modifier.width(4.dp))
            Text("글쓰기", fontWeight = FontWeight.Bold)
        }
    }
}
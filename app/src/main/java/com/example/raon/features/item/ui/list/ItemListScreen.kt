package com.example.raon.features.item.ui.list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.raon.features.main.ui.MainViewModel

@Composable
fun ItemListScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemListViewModel = hiltViewModel(),
    mainviewModel: MainViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit,
    onItemClick: (Int) -> Unit  // ItemDetail 페이지로 이동 이벤트
) {


    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {

        // uiState.items 상테로 바꾸기 -> itemList를 가져와서 UI로 보여줌
        ItemList(
            items = uiState.items,
            onItemClick = onItemClick
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
    onNavigateToSearch: () -> Unit,
    address: String
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = address, fontWeight = FontWeight.Bold, fontSize = 20.sp)
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

// itemList
@Composable
fun ItemList(
    items: List<ItemUiModel>,
    onItemClick: (Int) -> Unit // 터치한 Item의 ID를 전달
) {
    LazyColumn {
        items(
            items = items, // 파라미터 사용
            key = { it.id }
        ) { item -> // 변수명 변경
            ItemListItem(
                item = item,
                onClick = { onItemClick(item.id) }

            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}


// 각 item Ui
@Composable
fun ItemListItem(
    item: ItemUiModel,
    onClick: () -> Unit // 터치 이벤트 -> ItemDetail 화면을 이동
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)   // 클릭함수 넣어주기
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
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 2
                )
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

            // [수정] 조회수, 댓글, 좋아요 표시
            if (item.viewCount > 0 || item.comments > 0 || item.likes > 0) {
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 기존 댓글 UI
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

                    // [수정] 조회수 아이콘 및 UI
                    if (item.viewCount > 0) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveRedEye, // 눈 모양 아이콘
                            contentDescription = "조회수",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(2.dp)) // 아이콘과 텍스트 간격
                        Text(text = item.viewCount.toString(), fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                    }


                    // 기존 좋아요 UI
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

//// 각 item Ui
//@Composable
//fun ItemListItem(
//    item: ItemUiModel,
//    onClick: () -> Unit // 터치 이벤트 -> ItemDetail 화면을 이동
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)   // 클릭함수 넣어주기
//            .padding(16.dp)
//    ) {
//        AsyncImage(
//            model = item.imageUrl,
//            contentDescription = item.title,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Box(modifier = Modifier.height(100.dp)) {
//            // 제목, 위치/시간, 가격
//            Column(
//                modifier = Modifier.align(Alignment.TopStart)
//            ) {
//                Text(
//                    text = item.title,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp,
//                    maxLines = 2
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "${item.location} · ${item.timeAgo}",
//                    color = Color.Gray,
//                    fontSize = 13.sp
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                if (item.price > 0) {
//                    Text(
//                        text = "%,d원".format(item.price),
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 17.sp
//                    )
//                }
//            }
//
//            // [수정] 조회수, 댓글, 좋아요 표시
//            if (item.viewCount > 0 || item.comments > 0 || item.likes > 0) {
//                Row(
//                    modifier = Modifier.align(Alignment.BottomEnd),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // [추가] 조회수 UI
//                    if (item.viewCount > 0) {
//                        Text(text = "조회 ${item.viewCount}", fontSize = 13.sp, color = Color.Gray)
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//
//                    // 기존 댓글 UI
//                    if (item.comments > 0) {
//                        Icon(
//                            Icons.Outlined.ChatBubbleOutline,
//                            contentDescription = "댓글",
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(2.dp))
//                        Text(text = item.comments.toString(), fontSize = 13.sp, color = Color.Gray)
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//
//                    // 기존 좋아요 UI
//                    if (item.likes > 0) {
//                        Icon(
//                            Icons.Outlined.FavoriteBorder,
//                            contentDescription = "좋아요",
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(2.dp))
//                        Text(text = item.likes.toString(), fontSize = 13.sp, color = Color.Gray)
//                    }
//                }
//            }
//        }
//    }
//}


//// 각 item Ui
//@Composable
//fun ItemListItem(
//    item: ItemUiModel,
//    onClick: () -> Unit // 터치 이벤트 -> ItemDetail 화면을 이동
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)   // 클릭함수 넣어주기
//            .padding(16.dp)
//    ) {
//        AsyncImage(
//            model = item.imageUrl,
//            contentDescription = item.title,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Box(modifier = Modifier.height(100.dp)) {
//            // 제목, 위치/시간, 가격
//            Column(
//                modifier = Modifier.align(Alignment.TopStart)
//            ) {
//                Text(
//                    text = item.title,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp,
//                    maxLines = 2
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "${item.location} · ${item.timeAgo}",
//                    color = Color.Gray,
//                    fontSize = 13.sp
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                if (item.price > 0) {
//                    Text(
//                        text = "%,d원".format(item.price),
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 17.sp
//                    )
//                }
//            }
//            // 댓글/좋아요
//            if (item.comments > 0 || item.likes > 0) {
//                Row(
//                    modifier = Modifier.align(Alignment.BottomEnd),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (item.comments > 0) {
//                        Icon(
//                            Icons.Outlined.ChatBubbleOutline,
//                            contentDescription = "댓글",
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(2.dp))
//                        Text(text = item.comments.toString(), fontSize = 13.sp, color = Color.Gray)
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                    if (item.likes > 0) {
//                        Icon(
//                            Icons.Outlined.FavoriteBorder,
//                            contentDescription = "좋아요",
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(2.dp))
//                        Text(text = item.likes.toString(), fontSize = 13.sp, color = Color.Gray)
//                    }
//                }
//            }
//        }
//    }
//}
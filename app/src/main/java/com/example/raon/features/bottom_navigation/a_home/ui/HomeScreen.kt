package com.example.raon.features.bottom_navigation.a_home.ui

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.AsyncImage

// 데이터 클래스
data class ProductItem(
    val id: Int,
    val title: String,
    val location: String,
    val timeAgo: String,
    val price: Int,
    val imageUrl: String,
    val comments: Int,
    val likes: Int
)

// 상단 바
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
            // 지역 선택
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "대자동", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "지역 선택")
            }
            Spacer(modifier = Modifier.weight(1f))
            // 아이콘 버튼들
            Row {
                IconButton(onClick = {
                    /* 검색 */
                    onNavigateToSearch()
                }) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
                IconButton(onClick = { /* 알림 */ }) {
                    Icon(Icons.Outlined.NotificationsNone, contentDescription = "알림")
                }
            }
        }
    }
}

// 화면 콘텐츠
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // ✨ 샘플 데이터 3개 추가
    val productList = remember {
        listOf(
            ProductItem(
                1,
                "도마뱀이야기 🦎",
                "반려동물모임",
                "20시간 전 활동",
                0,
                "https://picsum.photos/id/1074/200/200",
                17,
                0
            ),
            ProductItem(
                2,
                "귀멸의 칼날 일륜도 키링 구해요..",
                "용두동",
                "1시간 전",
                12000,
                "https://picsum.photos/id/106/200/200",
                0,
                0
            ),
            ProductItem(
                3,
                "라퍼지스토어 미니멀 자켓",
                "신원동",
                "3시간 전",
                10000,
                "https://picsum.photos/id/177/200/200",
                0,
                0
            ),
            ProductItem(
                4,
                "(~10/12) 컨버스 척 70 블랙",
                "삼송동",
                "47분 전",
                10000,
                "https://picsum.photos/id/21/200/200",
                2,
                8
            ),
            ProductItem(
                5,
                "이케아 LACK 선반 화이트",
                "일산동",
                "1일 전",
                5000,
                "https://picsum.photos/id/25/200/200",
                3,
                5
            ),
            ProductItem(
                6,
                "몬스테라 분양합니다",
                "중산동",
                "끌올 10분 전",
                15000,
                "https://picsum.photos/id/1015/200/200",
                1,
                7
            ),
            ProductItem(
                7,
                "닌텐도 스위치 라이트 블루",
                "탄현동",
                "5시간 전",
                180000,
                "https://picsum.photos/id/17/200/200",
                8,
                15
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        ProductList(products = productList)
    }
}

// 글쓰기 버튼
@Composable
fun WritePostFab() {
    FloatingActionButton(
        onClick = { /* 글쓰기 화면으로 이동 */ },
        containerColor = Color(0xFFF76707), // 주황색
        contentColor = Color.White,
        shape = RoundedCornerShape(50) // 둥근 사각형 모양
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


// --- 이하 부속 Composable 함수들 ---

@Composable
fun ProductList(products: List<ProductItem>) {
    LazyColumn {
        items(products) { product ->
            ProductListItem(item = product)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

@Composable
fun ProductListItem(item: ProductItem) {
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
                        maxLines = 2
                    )
                    IconButton(onClick = { /* 더보기 */ }, modifier = Modifier.size(24.dp)) {
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


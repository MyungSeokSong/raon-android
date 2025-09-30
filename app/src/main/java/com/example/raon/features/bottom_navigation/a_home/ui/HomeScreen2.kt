package com.example.raon.features.bottom_navigation.a_home.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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

// ✨ 1. 상단 바 정의 (MainView에서 호출해서 사용)
@Composable
fun HomeTopAppBar2() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "내 동네", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "동네 선택")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            IconButton(onClick = { /* 검색 */ }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "검색",
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = { /* 알림 */ }) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = "알림",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ✨ 2. 화면 콘텐츠 (Scaffold 없이 순수 콘텐츠만 정의)
@Composable
fun HomeScreen2(modifier: Modifier = Modifier) {
    // 샘플 데이터
    val productList = remember {
        listOf(
            ProductItem(
                1,
                "QCY HT10 AilyBuds 무선 이어폰",
                "화정동",
                "14시간 전",
                15000,
                "https://picsum.photos/id/10/200/200",
                1,
                3
            ),
            ProductItem(
                2,
                "qcy t13 이어폰",
                "원흥동",
                "17일 전",
                20000,
                "https://picsum.photos/id/20/200/200",
                0,
                0
            ),
            ProductItem(
                3,
                "QCY ailybids pro+ 오픈형 이어폰",
                "지축동",
                "19시간 전",
                20000,
                "https://picsum.photos/id/30/200/200",
                0,
                2
            ),
            ProductItem(
                4,
                "QCY 블루투스 이어폰 팔아요",
                "삼송동",
                "2일 전",
                13000,
                "https://picsum.photos/id/40/200/200",
                5,
                10
            ),
            ProductItem(
                5,
                "깨끗한 QCY-T1 판매합니다",
                "도내동",
                "5일 전",
                10000,
                "https://picsum.photos/id/50/200/200",
                2,
                8
            )
        )
    }

    Column(modifier = modifier) {
        CategoryChips()
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
        ProductList(products = productList)
    }
}

// ✨ 3. 글쓰기 버튼 (MainView에서 호출해서 사용)
@Composable
fun WritePostFab() {
    FloatingActionButton(
        onClick = { /* 글쓰기 화면으로 이동 */ },
        containerColor = Color(0xFFF76707), // 주황색
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Icon(Icons.Default.Add, contentDescription = "글쓰기")
    }
}


// --- 이하 부속 Composable 함수들 ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips() {
    val categories = listOf("디지털기기", "생활가전", "가구/인테리어", "유아동", "여성의류", "스포츠/레저", "도서")
    var selectedCategory by remember { mutableStateOf("디지털기기") }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { selectedCategory = category },
                label = { Text(category) },
                enabled = true,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                ),
            )
        }
    }
}

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
        Column(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
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
                Text(
                    text = "%,d원".format(item.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}


// 미리보기용 Composable
@Preview(showBackground = true)
@Composable
fun HomeScreen2Preview() {
    // MainView에서 조립될 모습을 미리보기 위해 Scaffold 사용
    Scaffold(
        topBar = { HomeTopAppBar2() },
        floatingActionButton = { WritePostFab() }
    ) { paddingValues ->
        HomeScreen2(modifier = Modifier.padding(paddingValues))
    }
}


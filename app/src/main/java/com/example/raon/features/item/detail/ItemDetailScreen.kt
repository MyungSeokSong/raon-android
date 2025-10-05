package com.example.raon.features.item.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.raon.features.bottom_navigation.a_home.ui.ProductItem

/**
 * 상세 페이지에 필요한 추가적인 판매자 정보 데이터 클래스
 */
data class SellerInfo(
    val profileImageUrl: String,
    val nickname: String,
    val mannerTemperature: Float,
    val address: String
)

// --- 화면의 각 부분을 구성하는 Composable 함수들 ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    product: ProductItem,
    seller: SellerInfo,
//    seller2: Seller
    onBackClick: () -> Unit // 뒤로가기 동작을 위한 콜백
) {
    Scaffold(
        topBar = { ProductDetailTopAppBar(onBackClick) },
        bottomBar = { ProductBottomBar(product) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Scaffold가 제공하는 패딩 적용
        ) {
            // 1. 상품 이미지
            item {
                ProductImage(imageUrl = product.imageUrl)
            }

            // 2. 판매자 프로필
            item {
                SellerProfile(seller = seller)
            }

            // 구분선
            item {
                Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            }

            // 3. 상품 정보 (제목, 설명 등)
            item {
                ProductInfo(product = product)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        },
        actions = {
            IconButton(onClick = { /* 홈 화면으로 이동 */ }) {
                Icon(Icons.Outlined.Home, contentDescription = "홈")
            }
            IconButton(onClick = { /* 더보기 메뉴 */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "더보기")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

@Composable
fun ProductImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "상품 이미지",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // 1:1 비율
        contentScale = ContentScale.Crop
    )
}

@Composable
fun SellerProfile(seller: SellerInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 사진
        AsyncImage(
            model = seller.profileImageUrl,
            contentDescription = "판매자 프로필 사진",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 닉네임, 주소
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = seller.nickname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = seller.address, color = Color.Gray, fontSize = 13.sp)
        }

        // 매너온도
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${seller.mannerTemperature}°C",
                color = Color(0xFF00897B), // 따뜻한 느낌의 청록색
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            // 매너온도 막대 그래프
            LinearProgressIndicator(
                progress = { seller.mannerTemperature / 100f },
                modifier = Modifier.width(40.dp),
                color = Color(0xFF00897B),
                trackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProductInfo(product: ProductItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // 아이템 간 간격
    ) {
        // 제목
        Text(
            text = product.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        // 카테고리, 시간
        Text(
            text = "디지털기기 · ${product.timeAgo}", // 카테고리는 예시입니다.
            color = Color.Gray,
            fontSize = 13.sp
        )

        // 상품 설명
        Text(
            text = "상품 설명글입니다. 이 컨버스는 한 번도 신지 않은 새 상품입니다.\n" +
                    "사이즈는 240이고 박스도 그대로 있습니다.\n" +
                    "쿨거래 하실 분 연락주세요. 직거래는 삼송역에서 가능합니다.",
            fontSize = 16.sp,
            lineHeight = 24.sp // 줄 간격을 넓혀 가독성 향상
        )

        // 조회수 등 통계
        Text(
            text = "채팅 ${product.comments} · 관심 ${product.likes} · 조회 123", // 조회수는 예시입니다.
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ProductBottomBar(product: ProductItem) {
    // Surface를 사용해 그림자 효과 적용
    Surface(shadowElevation = 8.dp) {
        Column {
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 관심(하트) 버튼
                var isLiked by remember { mutableStateOf(false) }
                IconButton(onClick = { isLiked = !isLiked }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "관심",
                        tint = if (isLiked) Color(0xFFF76707) else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                // 세로 구분선
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                Spacer(Modifier.width(16.dp))

                // 가격 정보
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "%,d원".format(product.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "가격 제안 불가", // 예시
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                // 채팅 버튼
                Button(
                    onClick = { /* 채팅하기 화면으로 이동 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF76707)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("채팅하기", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}





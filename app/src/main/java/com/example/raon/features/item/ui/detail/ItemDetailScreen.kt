package com.example.raon.features.item.ui.detail

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

// 채팅하기 버튼 색
private val BrandYellow = Color(0xFFFDCC31)
private val DarkGrayText = Color(0xFF3C3C3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    onBackClick: () -> Unit,
    onNavigateToChatRoom: (Long) -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 내 물품인지 확인
    val isMine = uiState.item?.isMine ?: false

    // ViewModel의 일회성 이벤트를 구독하고 처리
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ItemChatEvent.NavigateToChatRoom -> {
                    // 전달받은 람다를 호출하여 화면 이동 요청
                    onNavigateToChatRoom(event.chatId)
                }

                is ItemChatEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ProductDetailTopAppBar(onBackClick = onBackClick, isMine, onMoreClick = {})
        },
        bottomBar = {
            if (!isMine) {
                uiState.item?.let { item ->
                    ProductBottomBar(
                        isFavorited = item.isFavorite,
                        onFavoriteClick = viewModel::onFavoriteButtonClicked,
//                    price = it.price,
                        onChatClick = {
                            viewModel.onChatButtonClicked()
                        }
                    )
                }
            }

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.item != null -> {
                    val item = uiState.item!!
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            ProductImagePager(imageUrls = item.imageUrls)
                        }
                        item {
                            SellerProfile(
                                nickname = item.sellerNickname,
                                profileUrl = item.sellerProfileUrl,
                                address = item.sellerAddress
                            )
                        }
                        item { Divider(color = Color.LightGray.copy(alpha = 0.5f)) }
                        item {
                            ProductInfo(
                                title = item.title,
                                price = item.price,
                                condition = item.condition,
                                category = "${item.category} ",  //·
                                time = "${item.createdAt}",
                                description = item.description,
                                stats = "관심 ${item.favoriteCount} · 조회 ${item.viewCount}"
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 이하 부속 Composable 함수들 ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailTopAppBar(
    onBackClick: () -> Unit,
    isMine: Boolean,    // 내 상품인지 확인
    onMoreClick: () -> Unit   // 더보기 버튼 클릭
) {
//    TopAppBar(
//        title = {
//            // isMine이 true일 때만 "내 상품" 텍스트를 표시합니다.
//            if (isMine) {
//                Text(
//                    text = "내 상품",
//                    fontWeight = FontWeight.Bold // 텍스트를 좀 더 강조하고 싶다면 추가
//                )
//            }
//        },
//        navigationIcon = {
//            IconButton(onClick = onBackClick) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
//            }
//        },
//        actions = {
//
//            if (isMine) {
//                IconButton(onClick = { /* TODO: 더보기 메뉴 */ }) {
//                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
//                }
//            }
//
////            IconButton(onClick = { /* TODO: 홈 화면 이동 */ }) {
////                Icon(Icons.Outlined.Home, contentDescription = "홈")
////            }
////            IconButton(onClick = { /* TODO: 더보기 메뉴 */ }) {
////                Icon(Icons.Default.MoreVert, contentDescription = "더보기")
////            }
//        }
//    )

    // TopAppBar -> CenterAlignedTopAppBar 로 변경
    CenterAlignedTopAppBar(
        title = {
            if (isMine) {
                Text(
                    text = "내 상품",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        },
        actions = {
            if (isMine) {
                IconButton(onClick = { /* TODO: 더보기 메뉴 */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductImagePager(imageUrls: List<String>) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("이미지가 없습니다.")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { pageIndex ->
            AsyncImage(
                model = imageUrls[pageIndex],
                contentDescription = "상품 이미지 ${pageIndex + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun SellerProfile(nickname: String, profileUrl: String?, address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = profileUrl,
            contentDescription = "판매자 프로필 사진",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = nickname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = address, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProductInfo(
    title: String,
    price: Int,
    condition: String,
    category: String,
    time: String,
    description: String,
    stats: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        // 👇 추가된 가격 Text 입니다.
        Text(
            text = "%,d원".format(price),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(text = category, color = Color.Gray, fontSize = 13.sp)
        Text(text = condition, color = Color.Gray, fontSize = 13.sp)
        Text(text = time, color = Color.Gray, fontSize = 13.sp)

        Text(text = description, fontSize = 16.sp, lineHeight = 24.sp)
        Text(text = stats, color = Color.Gray, fontSize = 13.sp)
    }
}


// ItemDetail BottomBar
@Composable
private fun ProductBottomBar(
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onChatClick: () -> Unit
) {
    // Surface를 사용하여 그림자 효과를 줍니다.
    Surface(shadowElevation = 8.dp) {
        // Column을 사용하여 상단 구분선과 버튼 영역을 나눕니다.
        Column {
            // 상단에 회색 구분선을 추가합니다.
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp), // 패딩을 조절합니다.
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 클릭 가능한 좋아요 아이콘 버튼
                IconButton(onClick = onFavoriteClick) {
                    /* TODO: 좋아요 기능 구현 */
                    Icon(
                        // 상태에 따라 이이콘 색상 수정
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "관심",
                        tint = if (isFavorited) Color.Red else Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp)) // 아이콘과 버튼 사이의 간격

                // 채팅하기 버튼
                Button(
                    onClick = onChatClick,
                    // weight(1f)를 사용하여 남은 가로 공간을 모두 차지하게 합니다.
                    modifier = Modifier.weight(1f),
                    // 👇 이 부분을 추가하여 버튼 색상을 지정합니다.
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellow, // 버튼 배경색
                        contentColor = DarkGrayText   // 버튼 안의 글자색
                    )
                ) {
                    Text("채팅하기")
                }
            }
        }
    }
}

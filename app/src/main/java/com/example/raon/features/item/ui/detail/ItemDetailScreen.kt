package com.example.raon.features.item.ui.detail

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 삭제 확인 다이얼로그 표시 여부를 관리하는 상태
    var showDeleteDialog by remember { mutableStateOf(false) }
    // 404 에러 팝업 상태 추가
    var showNotFoundErrorDialog by remember { mutableStateOf(false) }

    // 내 물품인지 확인
    val isMine = uiState.item?.isMine ?: false

    // ViewModel의 일회성 이벤트를 구독하고 처리
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ItemDetailEvent.NavigateToChatRoom -> {
                    onNavigateToChatRoom(event.chatId)
                }

                // ProductDeleted 이벤트 처리 추가
                is ItemDetailEvent.ProductDeleted -> {
                    Toast.makeText(context, "상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    onBackClick() // 이전 화면으로 이동
                }

                // 404 에러 이벤트를 받으면 팝업을 띄우도록 상태 변경
                is ItemDetailEvent.ShowProductNotFoundError -> {
                    showNotFoundErrorDialog = true
                }


                is ItemDetailEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    // [ 404 에러 AlertDialog 추가 ]
    if (showNotFoundErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                // 팝업 바깥을 눌러도 뒤로 가도록 처리
                showNotFoundErrorDialog = false
                onBackClick()
            },
            title = { Text("알림") },
            text = { Text("존재하지 않는 상품이거나 삭제되었습니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotFoundErrorDialog = false
                        onBackClick() // '확인' 버튼 누르면 뒤로가기
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }


    // ModalBottomSheet를 조건부로 표시합니다.
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                // 수정
                ListItem(
                    headlineContent = { Text("수정", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Filled.Edit, contentDescription = "수정") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // TODO: 수정 화면으로 이동하는 로직 구현
                            Toast.makeText(context, "수정", Toast.LENGTH_SHORT).show()
                            scope.launch { sheetState.hide() }
                        }
                )
                Divider()
                // 상태 변경
                ListItem(
                    headlineContent = { Text("상태 변경", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Filled.SwapHoriz, contentDescription = "상태 변경") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // TODO: 상태 변경 로직 구현
                            Toast.makeText(context, "상태 변경", Toast.LENGTH_SHORT).show()
                            scope.launch { sheetState.hide() }
                        }
                )
                Divider()
                // 삭제
                ListItem(
                    headlineContent = {
                        Text(
                            "삭제",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "삭제",
                            tint = Color.Red
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() } // 바텀 시트 먼저 닫기
                            showDeleteDialog = true // 삭제 다이얼로그 표시
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 닫기 버튼
                Button(
                    onClick = { scope.launch { sheetState.hide() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.5f),
                        contentColor = Color.Black
                    )
                ) {
                    Text("닫기", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // 삭제 확인 AlertDialog 추가
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                // 다이얼로그 바깥 클릭 또는 뒤로가기 시 다이얼로그 숨김
                showDeleteDialog = false
            },
            title = {
                Text(text = "게시글을 삭제할까요?", fontWeight = FontWeight.Bold)
            },
            confirmButton = {
                // 삭제 버튼 (빨간색)
                Button(
                    onClick = {
                        // TODO 주석을 viewModel.deleteProduct() 호출로 변경
                        viewModel.deleteProduct()
                        showDeleteDialog = false // 다이얼로그는 바로 닫기
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                // 취소 버튼
                TextButton(
                    onClick = {
                        showDeleteDialog = false // 다이얼로그 닫기
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.DarkGray)
                ) {
                    Text("취소")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            ProductDetailTopAppBar(
                onBackClick = onBackClick,
                isMine = isMine,
                onMoreClick = {
                    // 더보기 버튼 클릭 시 바텀 시트 표시
                    scope.launch { sheetState.show() }
                }
            )
        },
        bottomBar = {
            if (!isMine) {
                uiState.item?.let { item ->
                    ProductBottomBar(
                        isFavorited = item.isFavorite,
                        onFavoriteClick = viewModel::onFavoriteButtonClicked,
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
                                category = "${item.category} ",
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
    isMine: Boolean,
    onMoreClick: () -> Unit
) {
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
                IconButton(onClick = onMoreClick) {
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

@Composable
private fun ProductBottomBar(
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Column {
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "관심",
                        tint = if (isFavorited) Color.Red else Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellow,
                        contentColor = DarkGrayText
                    )
                ) {
                    Text("채팅하기")
                }
            }
        }
    }
}
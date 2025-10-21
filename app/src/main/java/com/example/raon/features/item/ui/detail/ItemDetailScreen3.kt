//package com.example.raon.features.item.ui.detail
//
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material.icons.outlined.FavoriteBorder
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil3.compose.AsyncImage
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//private val BrandYellow = Color(0xFFFDCC31)
//private val DarkGrayText = Color(0xFF3C3C3C)
//
//data class ImageAnalysisResult(
//    val imageId: Long,
//    val imageUrl: String,
//    val result: String,
//    val similarImages: List<String> = emptyList()
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ItemDetailScreen(
//    onBackClick: (isFavorite: Boolean) -> Unit,
//    onNavigateToChatRoom: (Long) -> Unit,
//    onNavigateToEdit: (itemId: Int) -> Unit,
//    viewModel: ItemDetailViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val moreOptionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    // --- 이미지 분석 UI를 위한 상태 변수 ---
//    var isAnalyzing by remember { mutableStateOf(false) }
//    var analysisResults by remember { mutableStateOf<List<ImageAnalysisResult>?>(null) }
//    val analysisSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    // --- 전체 화면 이미지 뷰어를 위한 상태 변수 ---
//    var showImageViewer by remember { mutableStateOf(false) }
//    var initialImageIndex by remember { mutableStateOf(0) }
//    // ---
//
//    var showDeleteDialog by remember { mutableStateOf(false) }
//    var showNotFoundErrorDialog by remember { mutableStateOf(false) }
//
//    val isMine = uiState.item?.isMine ?: false
//
//    // ... (LaunchedEffect, AlertDialog 등 기존 코드는 그대로)
//    // ...
//
//    if (analysisResults != null) {
//        ModalBottomSheet(
//            onDismissRequest = {
//                scope.launch {
//                    analysisSheetState.hide()
//                    analysisResults = null
//                }
//            },
//            sheetState = analysisSheetState
//        ) {
//            AnalysisResultBottomSheetContent(
//                results = analysisResults!!,
//                onClose = {
//                    scope.launch {
//                        analysisSheetState.hide()
//                        analysisResults = null
//                    }
//                }
//            )
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            ProductDetailTopAppBar(
//                onBackClick = { onBackClick(uiState.item?.isFavorite ?: false) },
//                isMine = isMine,
//                onMoreClick = { scope.launch { moreOptionSheetState.show() } }
//            )
//        },
//        bottomBar = {
//            if (!isMine) {
//                uiState.item?.let { item ->
//                    ProductBottomBar(
//                        isFavorited = item.isFavorite,
//                        onFavoriteClick = viewModel::onFavoriteButtonClicked,
//                        onChatClick = viewModel::onChatButtonClicked
//                    )
//                }
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            when {
//                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                uiState.errorMessage != null -> Text(
//                    uiState.errorMessage!!,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//
//                uiState.item != null -> {
//                    val item = uiState.item!!
//                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                        item {
//                            ProductImagePager(
//                                imageUrls = item.imageUrls,
//                                onImageClick = { index ->
//                                    initialImageIndex = index
//                                    showImageViewer = true
//                                }
//                            )
//                        }
//                        item {
//                            SellerProfile(
//                                nickname = item.sellerNickname,
//                                profileUrl = item.sellerProfileUrl,
//                                address = item.sellerAddress
//                            )
//                        }
//                        item { Divider(color = Color.LightGray.copy(alpha = 0.5f)) }
//                        item {
//                            ProductInfo(
//                                title = item.title,
//                                price = item.price,
//                                condition = item.condition,
//                                category = item.category,
//                                time = item.createdAt,
//                                description = item.description,
//                                stats = "관심 ${item.favoriteCount} · 조회 ${item.viewCount}"
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // ✨ 전체 화면 이미지 뷰어 (showImageViewer가 true일 때만 전체 화면을 덮음)
//    if (showImageViewer) {
//        FullScreenImageViewer(
//            imageUrls = uiState.item?.imageUrls ?: emptyList(),
//            initialPage = initialImageIndex,
//            isAnalyzing = isAnalyzing,
//            onAnalyzeClick = {
//                scope.launch {
//                    isAnalyzing = true
//                    delay(3000) // 3초 딜레이로 AI 분석 시뮬레이션
//                    analysisResults = listOf(
//                        ImageAnalysisResult(
//                            1L,
//                            uiState.item?.imageUrls?.getOrElse(0) { "" } ?: "",
//                            "SAFE"),
//                        ImageAnalysisResult(
//                            2L, uiState.item?.imageUrls?.getOrElse(1) { "" } ?: "", "WARNING",
//                            similarImages = listOf(
//                                "https://via.placeholder.com/150/FF0000/FFFFFF?Text=Similar+1",
//                                "https://via.placeholder.com/150/0000FF/FFFFFF?Text=Similar+2"
//                            )
//                        ),
//                        ImageAnalysisResult(
//                            3L,
//                            uiState.item?.imageUrls?.getOrElse(2) { "" } ?: "",
//                            "SAFE")
//                    )
//                    showImageViewer = false // 분석 후 뷰어 닫기
//                    isAnalyzing = false
//                    analysisSheetState.show() // 결과 BottomSheet 보여주기
//                }
//            },
//            onClose = { showImageViewer = false }
//        )
//    }
//}
//
//
//// ✨ [수정] 이미지 페이저 - 이미지를 클릭하는 역할만 하도록 단순화
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun ProductImagePager(
//    imageUrls: List<String>,
//    onImageClick: (index: Int) -> Unit
//) {
//    if (imageUrls.isEmpty()) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f)
//                .background(Color.LightGray),
//            contentAlignment = Alignment.Center
//        ) { Text("이미지가 없습니다.") }
//        return
//    }
//
//    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
//
//    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f)
//        ) { pageIndex ->
//            AsyncImage(
//                model = imageUrls[pageIndex],
//                contentDescription = "상품 이미지 ${pageIndex + 1}",
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clickable { onImageClick(pageIndex) }, // 클릭 시 전체 화면 뷰어 열기
//                contentScale = ContentScale.Crop
//            )
//        }
//        // 페이저 인디케이터
//        Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
//            repeat(pagerState.pageCount) { iteration ->
//                val color =
//                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
//                Box(
//                    modifier = Modifier
//                        .padding(2.dp)
//                        .clip(CircleShape)
//                        .background(color)
//                        .size(8.dp)
//                )
//            }
//        }
//    }
//}
//
//// ✨ [신규] 전체 화면 이미지 뷰어 Composable
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun FullScreenImageViewer(
//    imageUrls: List<String>,
//    initialPage: Int,
//    isAnalyzing: Boolean,
//    onAnalyzeClick: () -> Unit,
//    onClose: () -> Unit
//) {
//    // 뒤로가기 버튼을 누르면 뷰어가 닫히도록 설정
//    BackHandler { onClose() }
//
//    val pagerState = rememberPagerState(
//        initialPage = initialPage,
//        pageCount = { imageUrls.size }
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .clickable(onClick = onClose) // 배경 클릭 시 닫기
//    ) {
//        // 이미지 페이저
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier.fillMaxSize()
//        ) { pageIndex ->
//            AsyncImage(
//                model = imageUrls[pageIndex],
//                contentDescription = "상품 이미지 ${pageIndex + 1}",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Fit // 전체 이미지가 보이도록 Fit으로 설정
//            )
//        }
//
//        // 닫기 버튼
//        IconButton(
//            onClick = onClose,
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(16.dp)
//        ) {
//            Icon(Icons.Default.Close, contentDescription = "닫기", tint = Color.White)
//        }
//
//        // 분석 버튼 (분석 중이 아닐 때만 보임)
//        if (!isAnalyzing) {
//            Button(
//                onClick = onAnalyzeClick,
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Black.copy(alpha = 0.6f),
//                    contentColor = Color.White
//                ),
//                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
//            ) {
//                Icon(
//                    Icons.Default.Search,
//                    contentDescription = "AI 분석",
//                    modifier = Modifier.size(18.dp)
//                )
//                Spacer(modifier = Modifier.width(6.dp))
//                Text("AI 이미지 분석", fontSize = 13.sp, fontWeight = FontWeight.Bold)
//            }
//        }
//
//        // 로딩 오버레이 (분석 중일 때만 보임)
//        AnimatedVisibility(
//            visible = isAnalyzing,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.matchParentSize()
//        ) {
//            Box(
//                modifier = Modifier.background(Color.Black.copy(alpha = 0.7f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    CircularProgressIndicator(color = Color.White)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text("AI가 이미지를 분석하고 있습니다...", color = Color.White, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun AnalysisResultBottomSheetContent(
//    results: List<ImageAnalysisResult>,
//    onClose: () -> Unit
//) {
//    Column(modifier = Modifier.padding(bottom = 32.dp)) {
//        Text(
//            "AI 이미지 분석 결과",
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .padding(16.dp)
//                .align(Alignment.CenterHorizontally)
//        )
//        LazyColumn(
//            contentPadding = PaddingValues(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            items(results) { result ->
//                val resultColor = when (result.result) {
//                    "SAFE" -> Color(0xFF2E7D32)
//                    "WARNING" -> Color(0xFFEF6C00)
//                    else -> Color(0xFFC62828)
//                }
//                val resultIcon = when (result.result) {
//                    "SAFE" -> Icons.Default.CheckCircle
//                    else -> Icons.Default.Warning
//                }
//                val resultText = when (result.result) {
//                    "SAFE" -> "안전"
//                    "WARNING" -> "도용 의심"
//                    else -> "위험"
//                }
//                Row(verticalAlignment = Alignment.Top) {
//                    AsyncImage(
//                        model = result.imageUrl,
//                        contentDescription = "분석 이미지",
//                        modifier = Modifier
//                            .size(80.dp)
//                            .clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Column(modifier = Modifier.weight(1f)) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(
//                                imageVector = resultIcon,
//                                contentDescription = resultText,
//                                tint = resultColor
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(resultText, color = resultColor, fontWeight = FontWeight.Bold)
//                        }
//                        if (result.similarImages.isNotEmpty()) {
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text("인터넷에서 발견된 유사 이미지:", fontSize = 12.sp, color = Color.Gray)
//                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                items(result.similarImages) { similarUrl ->
//                                    AsyncImage(
//                                        model = similarUrl,
//                                        contentDescription = "유사 이미지",
//                                        modifier = Modifier
//                                            .size(60.dp)
//                                            .clip(RoundedCornerShape(4.dp)),
//                                        contentScale = ContentScale.Crop
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = onClose,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.LightGray.copy(alpha = 0.5f),
//                contentColor = Color.Black
//            )
//        ) { Text("확인", fontWeight = FontWeight.Bold) }
//    }
//}
//
//// --- 이하 다른 Composable 함수들은 변경 없음 (생략되지 않음) ---
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun ProductDetailTopAppBar(
//    onBackClick: () -> Unit,
//    isMine: Boolean,
//    onMoreClick: () -> Unit
//) {
//    CenterAlignedTopAppBar(
//        title = { if (isMine) Text("내 상품", fontWeight = FontWeight.Bold) },
//        navigationIcon = {
//            IconButton(onClick = onBackClick) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
//            }
//        },
//        actions = {
//            if (isMine) {
//                IconButton(onClick = onMoreClick) {
//                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
//                }
//            }
//        },
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
//    )
//}
//
//@Composable
//private fun SellerProfile(nickname: String, profileUrl: String?, address: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        AsyncImage(
//            model = profileUrl,
//            contentDescription = "판매자 프로필 사진",
//            modifier = Modifier
//                .size(48.dp)
//                .clip(CircleShape)
//                .background(Color.LightGray),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(Modifier.width(12.dp))
//        Column(Modifier.weight(1f)) {
//            Text(nickname, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//            Text(address, color = Color.Gray, fontSize = 13.sp)
//        }
//    }
//}
//
//@Composable
//private fun ProductInfo(
//    title: String,
//    price: Int,
//    condition: String,
//    category: String,
//    time: String,
//    description: String,
//    stats: String
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//        Text("%,d원".format(price), fontWeight = FontWeight.Bold, fontSize = 18.sp)
//        Text(category, color = Color.Gray, fontSize = 13.sp)
//        Text(condition, color = Color.Gray, fontSize = 13.sp)
//        Text(time, color = Color.Gray, fontSize = 13.sp)
//        Text(description, fontSize = 16.sp, lineHeight = 24.sp)
//        Text(stats, color = Color.Gray, fontSize = 13.sp)
//    }
//}
//
//@Composable
//private fun ProductBottomBar(
//    isFavorited: Boolean,
//    onFavoriteClick: () -> Unit,
//    onChatClick: () -> Unit
//) {
//    Surface(shadowElevation = 8.dp) {
//        Column {
//            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onFavoriteClick) {
//                    Icon(
//                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
//                        contentDescription = "관심",
//                        tint = if (isFavorited) Color.Red else Color.Unspecified,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }
//                Spacer(Modifier.width(16.dp))
//                Button(
//                    onClick = onChatClick,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = BrandYellow,
//                        contentColor = DarkGrayText
//                    )
//                ) { Text("채팅하기") }
//            }
//        }
//    }
//}
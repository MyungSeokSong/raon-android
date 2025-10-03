package com.example.raon.features.search.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold // 👈 Scaffold import
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

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


// ✨ 새로 추가된 필터 칩 Row
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChipsRow() {
    val categories = listOf("전체", "중고거래", "동네생활", "동네업체", "업체") // Image 1 기준
    var selectedCategory by remember { mutableStateOf("중고거래") } // 기본 선택

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), // 상단 여백 조절
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
                    selectedContainerColor = MaterialTheme.colorScheme.onSurface, // 선택 시 어둡게
                    selectedLabelColor = Color.White,
                    containerColor = Color.LightGray.copy(alpha = 0.5f), // 기본 배경색
                    labelColor = Color.Black // 기본 글씨색
                ),
                shape = RoundedCornerShape(20.dp), // 둥근 모양
                border = null // 보더 없음
            )
        }
    }
}

// 화면 콘텐츠
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(modifier: Modifier = Modifier) {
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
                5,
                "QCY 블루투스 이어폰 팔아요",
                "삼송동",
                "2일 전",
                13000,
                "https://picsum.photos/id/40/200/200",
                5,
                10
            ),
            ProductItem(
                6,
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

    var showSortBottomSheet by remember { mutableStateOf(false) }
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var showLocationBottomSheet by remember { mutableStateOf(false) }
    var showPriceBottomSheet by remember { mutableStateOf(false) }

    val sortBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categoryBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val locationBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val priceBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }

    // ✅ Scaffold 적용
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchAppBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* 검색 로직 */ },
                onBackClick = { /* 뒤로가기 */ },
                onHomeClick = { /* 홈으로 */ }
            )
        },
        floatingActionButton = {
            WritePostFab()
        }
    ) { paddingValues -> // ✅ Scaffold가 제공하는 패딩 값
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // ✅ 패딩 적용
        ) {
            FilterControls(
                onSortClick = { showSortBottomSheet = true },
                onCategoryClick = { showCategoryBottomSheet = true },
                onLocationClick = { showLocationBottomSheet = true },
                onPriceClick = { showPriceBottomSheet = true }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            ProductList(products = productList)
        }
    }


    // 바텀 시트들은 Scaffold 외부에 두어 전체 화면을 덮도록 합니다.
    if (showSortBottomSheet) {
        SortBottomSheet(
            sheetState = sortBottomSheetState,
            onDismissRequest = {
                scope.launch { sortBottomSheetState.hide() }.invokeOnCompletion {
                    if (!sortBottomSheetState.isVisible) showSortBottomSheet = false
                }
            }
        )
    }

    if (showCategoryBottomSheet) {
        CategoryFilterBottomSheet(
            sheetState = categoryBottomSheetState,
            onDismissRequest = {
                scope.launch { categoryBottomSheetState.hide() }.invokeOnCompletion {
                    if (!categoryBottomSheetState.isVisible) showCategoryBottomSheet = false
                }
            }
        )
    }

    if (showLocationBottomSheet) {
        SimpleBottomSheet(
            title = "지역 선택",
            content = { Text("지역 선택 내용은 여기에 들어갑니다.") },
            sheetState = locationBottomSheetState,
            onDismissRequest = {
                scope.launch { locationBottomSheetState.hide() }.invokeOnCompletion {
                    if (!locationBottomSheetState.isVisible) showLocationBottomSheet = false
                }
            }
        )
    }

    if (showPriceBottomSheet) {
        SimpleBottomSheet(
            title = "가격 설정",
            content = { Text("가격 설정 내용은 여기에 들어갑니다.") },
            sheetState = priceBottomSheetState,
            onDismissRequest = {
                scope.launch { priceBottomSheetState.hide() }.invokeOnCompletion {
                    if (!priceBottomSheetState.isVisible) showPriceBottomSheet = false
                }
            }
        )
    }
}

// 글쓰기 버튼
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


// --- 이하 부속 Composable 함수들은 변경 없음 ---

@Composable
fun FilterControls(
    onSortClick: () -> Unit,
    onLocationClick: () -> Unit,
    onPriceClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    var isChecked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier
                    .width(36.dp)
                    .height(20.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                ),
                thumbContent = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("판매중만 보기", fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterDropdownButton(text = "최신순", onClick = onSortClick)
            FilterDropdownButton(text = "대자동 외 44", onClick = onLocationClick)
            FilterDropdownButton(text = "가격", onClick = onPriceClick)
            FilterDropdownButton(text = "카테고리", onClick = onCategoryClick)
        }
    }
}

@Composable
fun FilterDropdownButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text, fontSize = 14.sp)
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
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
            modifier = Modifier.height(100.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                Text(
                    text = "%,d원".format(item.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            if (item.comments > 0 || item.likes > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    val sortOptions = listOf("추천순", "최신순", "낮은 가격순", "높은 가격순", "가까운순")
    var selectedSortOption by remember { mutableStateOf("추천순") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(100))
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "정렬",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            sortOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSortOption = option }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = if (selectedSortOption == option) MaterialTheme.colorScheme.primary else Color.Black,
                            fontWeight = if (selectedSortOption == option) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (option == "추천순") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "정보",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF76707)
                            )
                        }
                    }

                    if (selectedSortOption == option) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "선택됨",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    val categories = listOf(
        "디지털기기", "생활가전", "가구/인테리어", "유아동", "유아도서", "생활/주방",
        "여성의류", "남성패션/잡화", "뷰티/미용", "스포츠/레저", "취미/게임/음반", "도서",
        "티켓/교환권", "가공식품", "반려동물용품", "식물", "기타", "삽니다"
    )
    val selectedCategories = remember { mutableStateListOf<String>() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(100))
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "카테고리",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedCategories.contains(category)) {
                                    selectedCategories.remove(category)
                                } else {
                                    selectedCategories.add(category)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedCategories.contains(category),
                            onCheckedChange = { isChecked ->
                                if (isChecked) selectedCategories.add(category)
                                else selectedCategories.remove(category)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = category, fontSize = 16.sp)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedCategories.clear() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.5f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("초기화", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("적용하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBottomSheet(
    title: String,
    content: @Composable () -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(100))
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
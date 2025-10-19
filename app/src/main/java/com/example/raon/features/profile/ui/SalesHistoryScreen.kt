package com.example.raon.features.profile.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp // dp를 사용하기 위해 import
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raon.core.ui.component.ItemListComponoents
import com.example.raon.core.ui.model.ItemListUiModel
import kotlinx.coroutines.launch

// 탭 제목 리스트 (서버 상태와 일치하도록 변경)
private val tabs = listOf("판매중", "예약중", "거래완료")

/**
 * 나의 판매내역 메인 화면
 * Scaffold, TopAppBar, Tabs, Pager를 포함하며 ViewModel로부터 상태를 받아 UI를 그림
 *
 * @param onBackClick 뒤로가기 버튼 클릭 시 호출될 콜백
 * @param onItemClick 판매내역의 각 아이템 클릭 시 호출될 콜백 (상품 id 전달)
 * @param viewModel Hilt를 통해 주입받는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SalesHistoryScreen(
    onBackClick: () -> Unit,
    onItemClick: (itemId: Int) -> Unit, // 파라미터 이름은 itemId로 유지
    viewModel: SalesHistoryViewModel = hiltViewModel()
) {
    // ViewModel의 uiState를 구독하여 상태 변경을 감지
    val uiState by viewModel.uiState.collectAsState()
    // Pager의 상태(현재 페이지 등)를 관리
    val pagerState = rememberPagerState { tabs.size }
    // 코루틴 스코프 (탭 클릭 시 Pager를 부드럽게 이동시키기 위해 사용)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("나의 판매내역") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // 1. 탭 메뉴
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            // 탭 클릭 시 해당 페이지로 애니메이션과 함께 스크롤
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            // 2. 탭에 따라 콘텐츠를 보여주는 수평 페이저
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // 현재 페이지 인덱스에 맞는 데이터 리스트를 uiState에서 선택
                val itemsToShow = when (page) {
                    0 -> uiState.sellingItems
                    1 -> uiState.reservedItems
                    2 -> uiState.soldItems
                    else -> emptyList()
                }

                // 선택된 데이터 리스트로 콘텐츠를 그림
                SalesListContent(
                    isLoading = uiState.isLoading,
                    items = itemsToShow,
                    emptyMessage = "'${tabs[page]}' 내역이 없습니다.",
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

/**
 * 각 탭의 실제 콘텐츠를 표시하는 Composable (기존 코드 대체)
 * 로딩 상태, 데이터 유무에 따라 다른 UI를 보여줌
 *
 * @param isLoading 데이터 로딩 중인지 여부
 * @param items 화면에 표시할 아이템 리스트
 * @param emptyMessage 아이템이 없을 때 보여줄 메시지
 * @param onItemClick 아이템 클릭 콜백
 */
@Composable
private fun SalesListContent(
    isLoading: Boolean,
    items: List<ItemListUiModel>,
    emptyMessage: String,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        // 변경된 부분: contentAlignment를 Alignment.TopCenter로 변경하고, 상단에 여백을 줍니다.
        contentAlignment = Alignment.TopCenter
    ) {
        // 1. 초기 로딩 상태: 데이터가 아직 하나도 없을 때만 로딩 인디케이터 표시
        if (isLoading && items.isEmpty()) {
            // 상단 여백 추가
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        // 2. 데이터가 없는 상태: 로딩이 끝났는데 아이템이 없으면 안내 메시지 표시
        else if (items.isEmpty()) {
            // 상단 여백 추가
            Text(text = emptyMessage, modifier = Modifier.padding(top = 16.dp))
        }
        // 3. 데이터가 있는 상태: 공통 컴포넌트를 사용하여 아이템 목록 표시
        else {
            ItemListComponoents(
                items = items,
                onItemClick = onItemClick // 전달받은 콜백을 그대로 넘겨줌
            )
        }
    }
}
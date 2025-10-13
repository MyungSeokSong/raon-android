package com.example.raon.features.category.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 카테고리 목록을 보여주는 재사용 가능한 화면입니다.
 * NavController를 통해 parentId를 받아 하위 카테고리를 표시합니다.
 *
 * @param navController 화면 간 탐색을 처리하는 NavController.
 * @param viewModel 이 화면의 상태와 로직을 관리하는 CategoryViewModel.
 */
@Composable
fun CategoryScreen(
    onCategoryClick: (categoryId: Long) -> Unit,
//    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    // ViewModel의 StateFlow를 구독합니다.
    // lifecycle-aware 방식으로 상태를 수집하여 안전하고 효율적입니다.
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    // 로딩 상태 처리: categories가 아직 로드되지 않았다면 로딩 인디케이터를 표시합니다.
    // 초기값이 emptyList()이므로, DB 조회가 끝나기 전까지 이 조건이 참이 됩니다.
    if (categories.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // 데이터가 있으면 LazyColumn으로 리스트를 보여줍니다.
        // LazyColumn은 화면에 보이는 아이템만 렌더링하여 성능을 최적화합니다.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp) // 좌우 패딩 추가
        ) {
            items(
                items = categories,
                key = { category -> category.categoryId } // 각 아이템의 고유 키 지정 (성능 최적화)
            ) { category ->
                // 각 카테고리 아이템 UI
                CategoryItem(
                    categoryName = category.name,
                    onItemClick = {

                        // 람다함수에 categoryId를 담아서 전달
                        onCategoryClick(category.categoryId)


                        // 아이템 클릭 시, 현재 카테고리의 id를 parentId로 넘겨주며
                        // 자기 자신(category_screen)을 다시 호출합니다.
//                        navController.navigate("category_screen?parentId=${category.categoryId}")
                    }
                )
            }
        }
    }
}

/**
 * 개별 카테고리 아이템을 표시하는 Composable.
 *
 * @param categoryName 표시할 카테고리의 이름.
 * @param onItemClick 아이템이 클릭되었을 때 호출될 람다 함수.
 */
@Composable
private fun CategoryItem(
    categoryName: String,
    onItemClick: () -> Unit
) {
    Text(
        text = categoryName,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick) // 클릭 이벤트 처리
            .padding(16.dp)
    )
}

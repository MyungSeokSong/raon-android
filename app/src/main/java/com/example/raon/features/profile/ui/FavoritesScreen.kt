package com.example.raon.features.profile.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raon.core.ui.component.ItemListComponoents
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onItemClick: (itemId: Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ❗️ `by` 키워드를 사용하지 않고 LaunchedEffect에서 직접 Flow를 구독합니다.
    LaunchedEffect(Unit) {
        // 1. 현재 화면의 SavedStateHandle에서 StateFlow를 가져옵니다.
        val resultFlow = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<Pair<Int, Boolean>?>("favorite_result", null)

        // 2. Flow가 null이 아닐 때만 구독을 시작합니다.
        resultFlow?.filterNotNull()?.collect { (itemId, isFavorite) ->
            // 3. ViewModel을 업데이트하고, 처리가 끝나면 즉시 null로 되돌려 중복 실행을 막습니다.
            viewModel.updateFavoriteStatusFromResult(itemId, isFavorite)
            navController.currentBackStackEntry?.savedStateHandle?.set("favorite_result", null)
        }
    }

    // ViewModel의 일회성 이벤트(예: Toast)를 구독하고 처리합니다.
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is FavoritesEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("관심 목록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
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
                        text = "오류가 발생했습니다: ${uiState.errorMessage}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                !uiState.isLoading && uiState.favoriteItems.isEmpty() -> {
                    Text(
                        text = "찜한 상품이 없습니다.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ItemListComponoents(
                        items = uiState.favoriteItems,
                        onItemClick = onItemClick,
                        isFavoriteList = true,
                        onFavoriteClick = viewModel::toggleFavoriteStatus
                    )
                }
            }
        }
    }
}
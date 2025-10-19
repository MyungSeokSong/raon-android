// features/profile/ui/FavoritesScreen.kt

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
import com.example.raon.core.ui.component.ItemListComponoents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onItemClick: (itemId: Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current // Toast를 위해 context 가져오기

    // ViewModel의 일회성 이벤트를 구독하고 처리하는 부분
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
                        // ViewModel의 함수를 하트 클릭 콜백에 연결
                        onFavoriteClick = viewModel::toggleFavoriteStatus
                    )
                }
            }
        }
    }
}
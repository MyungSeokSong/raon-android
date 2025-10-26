package com.example.raon.features.profile.ui

import android.util.Log
import androidx.compose.foundation.Image // ▼▼▼ [수정] 기본 Image import 추가 ▼▼▼
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.raon.R
import com.example.raon.ui.theme.BrandDarkText
import com.example.raon.ui.theme.BrandYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSelectionScreen(
    onBackClick: () -> Unit,
    onBuyerSelected: (buyerId: Int?) -> Unit,
    viewModel: BuyerSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedBuyerId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("구매자 선택") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { selectedBuyerId?.let(onBuyerSelected) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedBuyerId != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellow,
                        contentColor = BrandDarkText
                    )
                ) {
                    Text("구매자 선택")
                }
                OutlinedButton(
                    onClick = {

                        Log.d(
                            "BuyerSelection_Click",
                            "1. '구매자 선택' button clicked with buyerId: $selectedBuyerId"
                        )
                        onBuyerSelected(null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("선택하지 않을래요")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    item { Text("최근 채팅 목록에서 구매자 찾기", modifier = Modifier.padding(16.dp)) }

                    items(uiState.buyers, key = { it.id }) { buyer ->
                        BuyerItem(
                            buyer = buyer,
                            isSelected = buyer.id == selectedBuyerId,
                            onClick = { selectedBuyerId = buyer.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BuyerItem(buyer: BuyerUiModel, isSelected: Boolean, onClick: () -> Unit) {
    Log.d("BuyerItem", "Rendering item for ${buyer.nickname}, Image URL: ${buyer.profileImageUrl}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ▼▼▼ [핵심 수정] if/else로 AsyncImage와 Image를 분리하여 사용 ▼▼▼

        // 두 이미지에 공통으로 적용할 Modifier
        val imageModifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.LightGray)

        if (buyer.profileImageUrl.isNullOrEmpty() || buyer.profileImageUrl == "http://default.profile") {
            // URL이 유효하지 않으면 -> 기본 `Image` 컴포저블과 `painterResource` 사용
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "${buyer.nickname}의 기본 프로필 사진",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        } else {
            // 유효한 URL이 있으면 -> `AsyncImage` 컴포저블 사용
            AsyncImage(
                model = buyer.profileImageUrl,
                contentDescription = "${buyer.nickname}의 프로필 사진",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Text(buyer.nickname)
    }
}
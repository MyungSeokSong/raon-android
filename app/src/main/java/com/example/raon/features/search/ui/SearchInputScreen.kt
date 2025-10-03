package com.example.raon.features.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchInputScreen(
    onNavigateToSearchResult: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember {
        mutableStateListOf("뉴발란스", "된장", "구두", "맥북 에어 m1")
    }
    val focusManager = LocalFocusManager.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            
//             상단 검색바 부분
            SearchAppBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    onNavigateToSearchResult()
                },      // 검색 이벤트
                onBackClick = {},
                onHomeClick = {}
            )


            Spacer(modifier = Modifier.height(16.dp))

            // 최근 검색어 섹션
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "최근 검색",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(recentSearches) { search ->
                        RecentSearchChip(
                            text = search,
                            onDelete = { recentSearches.remove(search) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchChip(
    text: String,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Delete search term",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }
        }
    }
}


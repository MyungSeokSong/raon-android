//package com.example.raon.features.category.ui
//
//package com.example.raon.features.item.ui.add
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.raon.navigation.Category
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CategorySelectionScreen(
//    navController: NavController, // 하위 카테고리로 이동하기 위한 NavController
//    onCategorySelected: (Category) -> Unit,
//    onBackClick: () -> Unit,
//    viewModel: CategoryViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("카테고리 선택", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(modifier = Modifier.padding(paddingValues)) {
//            items(uiState.categories) { category ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            if (category.hasChildren) {
//                                // 하위 카테고리가 있으면, 자기 자신을 다시 호출하여 다음 층으로 이동
//                                navController.navigate("categorySelection?parentId=${category.id}")
//                            } else {
//                                // 최종 카테고리이면, 결과를 반환
//                                onCategorySelected(category)
//                            }
//                        }
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(text = category.name, modifier = Modifier.weight(1f))
//                    if (category.hasChildren) {
//                        Icon(Icons.Default.ChevronRight, contentDescription = "하위 카테고리")
//                    }
//                }
//                HorizontalDivider()
//            }
//        }
//    }
//}
//

package com.example.raon.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.raon.features.category.ui.CategoryScreen
import com.example.raon.features.category.ui.CategoryViewModel
import com.example.raon.features.chat.ui.ChatRoomScreen
import com.example.raon.features.item.ui.add.AddItemEvent
import com.example.raon.features.item.ui.add.AddItemScreen
import com.example.raon.features.item.ui.add.AddItemViewModel
import com.example.raon.features.item.ui.detail.ItemDetailScreen
import com.example.raon.features.profile.ui.FavoritesScreen
import com.example.raon.features.profile.ui.SalesHistoryScreen
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen
import com.example.raon.features.user.ui.ProfileEditScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth_graph") {

        composable(
            "category?parentId={parentId}&path={path}",
            arguments = listOf(
                navArgument("parentId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("path") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val viewModel: CategoryViewModel = hiltViewModel()
            CategoryScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    if (category.isLeaf) {
                        // 'addItem' 화면으로 결과를 전달합니다.
//                        navController.previousBackStackEntry?.savedStateHandle?.apply {
//                            set("selectedCategoryName", category.name)
//                            set("selectedCategoryId", category.categoryId)

                        // ❗️ [핵심 수정 1]
                        // '이전' 화면이 아닌, 'addItem' 화면의 SavedStateHandle을 직접 찾아서 데이터를 전달합니다.
                        val addItemRoute = "addItem?itemId={itemId}"
                        navController.getBackStackEntry(addItemRoute)?.savedStateHandle?.apply {
                            set("selectedCategoryName", category.name)
                            set("selectedCategoryId", category.categoryId)

                        }

                        // ❗️ [핵심 수정 2]
                        // 'addItem' 화면이 나올 때까지 모든 카테고리 화면을 한 번에 닫습니다.
                        // inclusive = false는 'addItem' 화면 자체는 닫지 않겠다는 의미입니다.
                        navController.popBackStack(addItemRoute, inclusive = false)


//                        navController.popBackStack()
                    } else {
                        val currentPath = viewModel.pathString2
                        val newPath = if (currentPath.isNullOrEmpty()) {
                            category.name
                        } else {
                            "$currentPath,${category.name}"
                        }
                        navController.navigate("category?parentId=${category.categoryId}&path=${newPath}")
                    }
                }
            )
        }

        authGraph(navController)
        mainGraph(navController)

        // ❗️ Item 등록 및 수정 뷰 (경로 수정)
        composable(
            // 👇 경로를 "addItem?itemId={itemId}" 형태로 변경하여 itemId를 선택적 파라미터로 만듭니다.
            route = "addItem?itemId={itemId}",
            arguments = listOf(navArgument("itemId") {
                type = NavType.IntType
                defaultValue = -1 // itemId가 없으면 -1이 전달되어 '등록 모드'로 인식됩니다.
            })
        ) { backStackEntry ->
            val addItemViewModel: AddItemViewModel = hiltViewModel()
            val categoryNameResult by backStackEntry.savedStateHandle
                .getStateFlow<String?>("selectedCategoryName", null)
                .collectAsStateWithLifecycle()
            val categoryIdResult by backStackEntry.savedStateHandle
                .getStateFlow<Long?>("selectedCategoryId", null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(categoryIdResult, categoryNameResult) {
                val categoryId = categoryIdResult?.toInt()
                val categoryName = categoryNameResult
                if (categoryId != null && categoryName != null) {
                    addItemViewModel.onEvent(
                        AddItemEvent.CategorySelected(
                            categoryId,
                            categoryName
                        )
                    )
                    backStackEntry.savedStateHandle.remove<String>("selectedCategoryName")
                    backStackEntry.savedStateHandle.remove<Long>("selectedCategoryId")
                }
            }

            AddItemScreen(
                modifier = modifier,
                onUploadSuccess = { navController.popBackStack() },
                onNavigationToCategory = { navController.navigate("category") },
                onClose = { navController.popBackStack() },
                onClearCategoryResult = {
                    backStackEntry.savedStateHandle.remove<String>("selectedCategoryName")
                    backStackEntry.savedStateHandle.remove<Long>("selectedCategoryId")
                }
            )
        }

        // ❗️ Item 상세보기 뷰 (수정 화면 호출 경로 수정)
        composable(
            route = "itemDetail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: -1

            ItemDetailScreen(
                onBackClick = { isFavorite ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("favorite_result", itemId to isFavorite)
                    navController.popBackStack()
                },
                onNavigateToChatRoom = { chatRoomId ->
                    navController.navigate("chatRoom/$chatRoomId")
                },
                onNavigateToEdit = { editItemId ->
                    // 수정 버튼 클릭 시, 바뀐 경로 형식에 맞게 호출합니다.
                    navController.navigate("addItem?itemId=$editItemId")
                }
            )


        }

        composable("chatRoom/{chatRoomId}") {
            ChatRoomScreen(
                onBackClick = { chatId ->
                    // 👇 [가장 중요] getBackStackEntry("main_graph")를 사용하는지 확인!
                    try {
                        Log.d(
                            "ChatReadDebug",
                            "2. AppNavigation trying to set result for 'main_graph'. chatId: $chatId"
                        )
                        navController.getBackStackEntry("main_graph")
                            .savedStateHandle
                            .set("read_chat_room_id", chatId)
                        Log.d("ChatReadDebug", "3. Successfully set the result.")
                    } catch (e: Exception) {
                        Log.e("ChatReadDebug", "Failed to get back stack entry 'main_graph'", e)
                    }
                    navController.popBackStack()
                }
            )
//            ChatRoomScreen2()
        }

        composable("searchInput") {
            SearchInputScreen(
                onNavigateToSearchResult = { query -> // ❗️ 파라미터 이름을 onSearch로 가정 (SearchInputScreen 정의에 맞게 수정 필요)
                    navController.navigate("searchResult/$query")
                },
                onCloses = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("main_graph") {
                        popUpTo("searchInput") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "searchResult/{query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultScreen(
                searchQuery = query,
                onItemClick = { itemId -> navController.navigate("itemDetail/$itemId") },
                onCloses = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("main_graph") {
                        popUpTo("searchInput") { inclusive = true }
                    }
                }
            )
        }

        composable("salesHistory") {
            SalesHistoryScreen(
                onItemClick = { itemId -> navController.navigate("itemDetail/$itemId") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                navController = navController,
                onItemClick = { itemId -> navController.navigate("itemDetail/$itemId") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 👇 [신규] 프로필 수정 화면 경로 (람다 방식으로 수정)
        composable("profileEdit") {
            ProfileEditScreen(
                // navController = navController // <-- ⛔️ 삭제
                onClose = { navController.popBackStack() } // 👈 [추가]
            )
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
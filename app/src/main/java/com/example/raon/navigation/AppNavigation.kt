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
// 👇 ItemDetailViewModel을 NavHost에서 직접 사용하기 위해 import 합니다.
import com.example.raon.features.item.ui.detail.ItemDetailViewModel
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
                        val addItemRoute = "addItem?itemId={itemId}"
                        navController.getBackStackEntry(addItemRoute)?.savedStateHandle?.apply {
                            set("selectedCategoryName", category.name)
                            set("selectedCategoryId", category.categoryId)
                        }
                        navController.popBackStack(addItemRoute, inclusive = false)
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

        // 👇 [핵심 수정 1] AddItemScreen의 onUploadSuccess 로직을 변경합니다.
        composable(
            route = "addItem?itemId={itemId}",
            arguments = listOf(navArgument("itemId") {
                type = NavType.IntType
                defaultValue = -1
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
                onUploadSuccess = {
                    // "성공했어요!" 신호를 받으면 NavHost가 직접 행동합니다.
                    // 1. 이전 화면(ItemDetail)의 SavedStateHandle에 표식을 남깁니다.
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("item_updated", true)
                    // 2. 현재 화면을 닫습니다.
                    navController.popBackStack()
                },
                onNavigationToCategory = { navController.navigate("category") },
                onClose = { navController.popBackStack() },
                onClearCategoryResult = {
                    backStackEntry.savedStateHandle.remove<String>("selectedCategoryName")
                    backStackEntry.savedStateHandle.remove<Long>("selectedCategoryId")
                }
            )
        }

        // 👇 [핵심 수정 2] ItemDetailScreen에 새로고침 로직을 추가합니다.
        composable(
            route = "itemDetail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->

            // 1. ItemDetailViewModel을 hilt를 통해 가져옵니다.
            val viewModel: ItemDetailViewModel = hiltViewModel()

            // 2. 현재 화면의 SavedStateHandle에서 "item_updated" 표식을 확인합니다.
            val shouldRefresh = backStackEntry.savedStateHandle.get<Boolean>("item_updated")

            // 3. ItemDetailScreen에는 ViewModel과 함께 "새로고침 필요" 여부와
            //    "표식 제거" 람다 함수를 전달합니다.
            ItemDetailScreen(
                viewModel = viewModel,
                shouldRefresh = shouldRefresh ?: false,
                onRefreshDone = {
                    backStackEntry.savedStateHandle.remove<Boolean>("item_updated")
                },
                onBackClick = { isFavorite ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            "favorite_result",
                            backStackEntry.arguments?.getInt("itemId") to isFavorite
                        )
                    navController.popBackStack()
                },
                onNavigateToChatRoom = { chatRoomId ->
                    navController.navigate("chatRoom/$chatRoomId")
                },
                onNavigateToEdit = { editItemId ->
                    navController.navigate("addItem?itemId=$editItemId")
                }
            )

            // ItemDetailScreen2 테스트
//            ItemDetailScreen(
//                onBackClick = { isFavorite ->
//                },
//                onNavigateToChatRoom = { chatRoomId ->
//                    navController.navigate("chatRoom/$chatRoomId")
//                },
//                onNavigateToEdit = { editItemId ->
//                    navController.navigate("addItem?itemId=$editItemId")
//                }
//            )
        }

        composable(
            route = "chatRoom/{chatRoomId}",
            arguments = listOf(
                navArgument("chatRoomId") {
                    type = NavType.StringType
                }
            )
        ) {
            ChatRoomScreen(
                onBackClick = { chatId ->
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
        }

        composable("searchInput") {
            SearchInputScreen(
                onNavigateToSearchResult = { query ->
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

        composable("profileEdit") {
            ProfileEditScreen(
                onClose = { navController.popBackStack() }
            )
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
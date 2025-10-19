package com.example.raon.navigation

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
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("selectedCategoryName", category.name)
                            set("selectedCategoryId", category.categoryId)
                        }
                        navController.popBackStack()
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

        composable("addItem") { backStackEntry ->
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
                }
            )
        }

        composable("chatRoom/{chatRoomId}") { ChatRoomScreen() }

        // ❗️ 검색 화면 수정
        composable("searchInput") {
            
            SearchInputScreen(
                // 👇 "onSearch =" 이름을 명시적으로 추가
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
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
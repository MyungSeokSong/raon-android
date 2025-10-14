package com.example.raon.navigation

//import com.example.raon.features.item.ui.detail.ItemDetailScreen
//import com.example.raon.features.item.ui.detail.SellerInfo
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
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    // navController 생성
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth_graph") {

        composable(
            "category?parentId={parentId}&path={path}",
            arguments = listOf(
                navArgument("parentId") {
                    type = NavType.IntType // 타입은 Int
                    defaultValue = -1    // 기본값은 -1
                },
                navArgument("path") {
                    type = NavType.StringType // 타입은 String
                    defaultValue = ""    // 기본값은 -1
                }

            )) {
            // 현재 화면의 ViewModel을 가져옵니다.
            val viewModel: CategoryViewModel = hiltViewModel()

            CategoryScreen(
                onBackClick = {
                    navController.popBackStack()    // 뒤로가기 버튼 눌렀을 때
                },
                { category ->

                    if (category.isLeaf) {

                        // 2. 이전 화면(AddItemScreen)의 SavedStateHandle에 접근해
                        //    전달할 데이터를 key-value 형태로 저장합니다.
                        navController.getBackStackEntry("addItem")?.savedStateHandle?.apply {
                            set("selectedCategoryName", category.name)
                            set("selectedCategoryId", category.categoryId)
                        }
                        // 3. 현재 화면을 닫고 이전 화면으로 돌아갑니다.
                        navController.popBackStack("addItem", inclusive = false)


                    } else {
                        // 1. 현재 화면의 경로를 ViewModel에서 가져옵니다. (예: "여성의류")
                        val currentPath = viewModel.pathString2

                        // 2. 새로운 전체 경로를 만듭니다.
                        val newPath = if (currentPath.isNullOrEmpty()) {
                            // 현재 경로가 없으면(최상위 화면), 클릭한 카테고리 이름이 새 경로가 됩니다.
                            category.name
                        } else {
                            // 현재 경로가 있으면(예: "여성의류"), 쉼표(,)로 연결합니다.
                            // 결과: "여성의류,아우터"
                            "$currentPath,${category.name}"
                        }

                        navController.navigate("category?parentId=${category.categoryId}&path=${newPath}")
                    }


//                    navController.navigate("category?parentId=${category.categoryId}&path=${category.name}")  // 수정전
                })
        }


        // 그래프
        authGraph(navController)    // auth 관련 화면
        mainGraph(navController)    // main 관련 화면


        // Item 등록 뷰
        composable("addItem") { backStackEntry ->

            // ✨ 1. AddItemViewModel의 인스턴스를 가져옵니다.
            val addItemViewModel: AddItemViewModel = hiltViewModel()


            // ✨ 2. backStackEntry의 SavedStateHandle에서 StateFlow로 데이터를 관찰합니다.
            val categoryNameResult by backStackEntry.savedStateHandle
                .getStateFlow<String?>("selectedCategoryName", null)
                .collectAsStateWithLifecycle()
            val categoryIdResult by backStackEntry.savedStateHandle
                .getStateFlow<Long?>("selectedCategoryId", null)
                .collectAsStateWithLifecycle()

            Log.d("카카테고리0", "카테고리 선택 이벤트 id! : ${categoryIdResult}")
            Log.d("카카테고리0", "카테고리 선택 이벤트 name! : ${categoryNameResult}")


            // ✨ 3. 결과가 도착했을 때 "딱 한 번만" ViewModel에 이벤트를 보냅니다.
            LaunchedEffect(categoryIdResult, categoryNameResult) {

                // 2. 받은 Long을 우리가 필요한 Int로 안전하게 변환합니다.
                val categoryId = categoryIdResult?.toInt()   // Long -> Int로 변환
                val categoryName = categoryNameResult

                if (categoryIdResult != null && categoryNameResult != null) {
                    addItemViewModel.onEvent(
                        AddItemEvent.CategorySelected(
                            categoryId!!,
                            categoryNameResult!!
                        )
                    )

                    Log.d("카카테고리1", "카테고리 선택 이벤트 id! : ${categoryIdResult}")
                    Log.d("카카테고리1", "카테고리 선택 이벤트 name! : ${categoryNameResult}")


                    // ViewModel에 전달 후에는 값을 지워서 중복 처리를 방지합니다.
                    backStackEntry.savedStateHandle.remove<String>("selectedCategoryName")
                    backStackEntry.savedStateHandle.remove<Int>("selectedCategoryId")
                }
            }


            AddItemScreen(
                modifier,
                // 여기에 ViewModel을 만들어서 전달하는 것이 더 좋습니다.
                // 우선은 UI에 바로 전달하는 예시입니다.
                onUploadSuccess = {
                    navController.popBackStack()
                    // 후에 업로드 성공시 ItemDetail 뷰 보이게 해야함
                },
                onNavigationToCategory = {
                    navController.navigate("category")
                },
                onClose = {          // onClose 파라미터에 뒤로 가기 동작을 전달
                    navController.popBackStack()
                },
                // (권장) ViewModel에 데이터를 전달했다면, 한 번 사용한 값은 제거합니다.
                onClearCategoryResult = {
                    backStackEntry.savedStateHandle.remove<String>("selectedCategoryName")
                    backStackEntry.savedStateHandle.remove<Int>("selectedCategoryId")
                }
            )
        }

        // Item 상세보기 뷰
        composable(
            route = "itemDetail/{itemId}",   // 경로에 변수가 포함됨을 정의
            arguments = listOf(navArgument("itemId") {
                type = NavType.IntType  // itemId는 Int 타입으로 정의
            })
        ) {
            ItemDetailScreen(
                onBackClick = {
                    navController.popBackStack()    // 뒤로가기 버튼 눌렀을 때
                },
                onNavigateToChatRoom = { chatRoomId ->  // 채팅방 ID 넘겨주기
                    navController.navigate("chatroom/$chatRoomId")
                }
            )
        }


        // 채팅 방 뷰
        composable("chatRoom/{chatRoomId}") { ChatRoomScreen() }

        // 설정 화면
//        composable("settings_screen") {
//            SettingsScreen({    // 로그아웃 화면 전환
//                navController.navigate("auth") {
//                    popUpTo("main") {
//                        inclusive = true
//                    }
//                }
//            }, navController)
//        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 검색 화면 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        composable("searchInput") {
            SearchInputScreen(
                { query ->

                    navController.navigate("searchResult/$query")
                },
                onCloses = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("main_graph") {
                        popUpTo("searchInput") { inclusive = true }
                    }
//                    navController.popBackStack()
                }
            )
        }

//        composable("searchResult") {
//            SearchResultScreen()
//        }

        // 2. 검색 결과 화면
        composable(
            route = "searchResult/{query}", // {query} 부분으로 검색어를 전달받음
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            // 전달받은 검색어를 추출
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultScreen(
                searchQuery = query,
                // 상세 페이지로 이동하는 로직 등 추가
                onItemClick = { itemId ->
                    /* TODO: 상세 페이지로 이동 */
                    navController.navigate("itemDetail/$itemId")
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


        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


//        composable("locationSetting") {
//            LocationSearchScreen()
//        }

    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
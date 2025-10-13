package com.example.raon.navigation

//import com.example.raon.features.item.ui.detail.ItemDetailScreen
//import com.example.raon.features.item.ui.detail.SellerInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.raon.features.category.ui.CategoryScreen
import com.example.raon.features.chat.ui.ChatRoomScreen
import com.example.raon.features.item.ui.add.AddItemScreen
import com.example.raon.features.item.ui.detail.ItemDetailScreen
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    // navController 생성
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "category") {

        composable("category") {
            CategoryScreen({ categoryId ->
                navController.navigate("category?parentId=${categoryId}")
            })
        }


        // 그래프
        authGraph(navController)    // auth 관련 화면
        mainGraph(navController)    // main 관련 화면


        // Item 등록 뷰
        composable("addItem") {
            AddItemScreen(
                modifier,
                onUploadSuccess = {
                    navController.popBackStack()
                    // 후에 업로드 성공시 ItemDetail 뷰 보이게 해야함
                },
                onClose = {          // onClose 파라미터에 뒤로 가기 동작을 전달
                    navController.popBackStack()
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
                { navController.navigate("searchResult") }
            )
        }

        composable("searchResult") {
            SearchResultScreen()
        }


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
package com.example.raon

//import com.example.raon.features.item.ui.detail.ItemDetailScreen
//import com.example.raon.features.item.ui.detail.SellerInfo
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.raon.features.auth.ui.AuthScreen
import com.example.raon.features.auth.ui.LoginScreen
import com.example.raon.features.auth.ui.SignUpScreen
import com.example.raon.features.auth.ui.z_etc.AuthView
import com.example.raon.features.auth.ui.z_etc.KakaoAuthViewModel
import com.example.raon.features.chat.ui.ChatRoomScreen
import com.example.raon.features.item.ui.add.AddItemScreen
import com.example.raon.features.item.ui.detail.ItemDetailScreen
import com.example.raon.features.location.ui.LocationSearchScreen
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen
import com.example.raon.features.splash.SplashScreen
import com.example.raon.features.user.ui.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: KakaoAuthViewModel,
    context: Context,
) {
    // navController 생성
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {


        // 로딩 첫 화면
        composable("splash") {
            SplashScreen(
                onNavigateToAuth = {    // Auth 홈 화면으로 이동 -> 토큰 없음
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {    // Main 화면으로 이동 -> 토큰 있음
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }


        composable("auth") {    // Auth 홈 화면
            AuthScreen(
                modifier,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToSignUp = { navController.navigate("signUp") },
                context
            )
        }

        composable("login") {   // 로그인 화면
            LoginScreen({
                navController.navigate("main") {

                    popUpTo("auth") {
                        inclusive = true
                    }
                }
            })
        }

        composable("signUp") {  // 회원가입 화면
            SignUpScreen({
                navController.navigate("main") {
                    popUpTo("auth") {
                        inclusive = true
                    }
                }
            }, {
                navController.navigate("locationSetting")
            })
        }

        composable("locationSetting") {
            LocationSearchScreen()
        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ Item 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        composable("main") {
            MainView(modifier, navController)
        }


        composable("addItem") {
            AddItemScreen(
                modifier,
                onUploadSuccess = {

                    navController.popBackStack()

//                    navController.navigate("itemDetail") {
//                        // 현재 화면인 "addItem"을 백 스택에서 제거
//                        popUpTo("addItem") {
//                            inclusive = true
//                        }
//                    }
                },
                // onClose 파라미터에 뒤로 가기 동작을 전달
                onClose = {
                    navController.popBackStack()
                }
            )
        }

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


        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 바텀 네비게이션 화면 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


        composable("chatroom/{chatRoomId}") {
            ChatRoomScreen()
        }
        composable("settings_screen") {
            SettingsScreen({    // 로그아웃 화면 전환
                navController.navigate("auth") {
                    popUpTo("main") {
                        inclusive = true
                    }
                }
            }, navController)
        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 검색 화면 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        composable("searchInput") {
            SearchInputScreen(
                { navController.navigate("searchResult") }
            )
        }

        composable("searchResult") {
            SearchResultScreen()
        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 기타 네비게이션 부분 ( 정리 필요 ) ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        composable("newAuthScreen") {
            AuthView(modifier, navController, viewModel, context)
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
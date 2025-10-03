package com.example.raon

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.raon.features.auth.ui.AuthScreen
import com.example.raon.features.auth.ui.LoginScreen
import com.example.raon.features.auth.ui.SignUpScreen
import com.example.raon.features.auth.ui.z_etc.AuthView
import com.example.raon.features.auth.ui.z_etc.KakaoAuthViewModel
import com.example.raon.features.bottom_navigation.c_add_item.ui.AddItemScreen
import com.example.raon.features.bottom_navigation.d_chat.ui.ChatRoomScreen
import com.example.raon.features.bottom_navigation.e_profile.ui.SettingsScreen
import com.example.raon.features.location.ui.LocationSearchScreen
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: KakaoAuthViewModel,
    context: Context,
) {
    // navController 생성
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth") {

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

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 바텀 네비게이션 화면 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        composable("main") {
            MainView(modifier, navController)
        }
        composable("addItem") {
            AddItemScreen()
        }
        composable("chatroom") {
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
package com.example.raon.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.raon.features.main.ui.MainView
import com.example.raon.features.user.ui.SettingsScreen

/**
 * '메인 층'에 해당하는 네비게이션 그래프입니다.
 * 로그인 후 진입하는 MainView와 설정 화면 등의 흐름을 관리합니다.
 */
fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(startDestination = "main_screen", route = "main_graph") {

        composable("main_screen") {
            // MainView는 '메인 층'의 입구 역할을 하며,
            // '별관'(addItem, itemDetail 등)으로 이동해야 하므로,
            // 건물 전체를 아는 상위 navController를 전달받습니다.
            MainView(navController = navController)
        }

        composable("settings_screen") {
            // 설정 화면도 '메인 층'의 일부입니다.
            SettingsScreen(
                onLogout = {
                    // 로그아웃 시, '메인 층'을 완전히 떠나 '인증 층'으로 이동합니다.
                    navController.navigate("auth_graph") {
                        popUpTo("main_graph") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    }
}


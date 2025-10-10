package com.example.raon.features.user.ui


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.raon.features.main.ui.MainView

//import com.example.raon.features.settings.ui.SettingsScreen

// 'main' 관련 화면들을 묶어주는 별도의 네비게이션 그래프
fun NavGraphBuilder.mainGraph(navController: NavController) {
    // "main_graph"라는 이름의 새로운 그룹을 만듭니다.
    // 이 그룹의 시작 화면은 "main"입니다.
    navigation(startDestination = "main", route = "main_graph") {

        composable("main") {
            // MainView는 이제 이 'main_graph' 그룹에 속합니다.
            MainView(navController = navController)
        }

        composable("settings_screen") {
            // SettingsScreen도 이 그룹에 속합니다.
            SettingsScreen(
                onLogout = {
                    // 로그아웃 시에는 그룹을 완전히 빠져나가 auth 화면으로 갑니다.
                    navController.navigate("auth") {
                        popUpTo("main_graph") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // TODO: MainView 안의 바텀 네비게이션으로 이동하는 다른 화면들도 여기에 추가
    }
}

package com.example.raon.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.raon.features.auth.ui.AuthScreen
import com.example.raon.features.auth.ui.LoginScreen
import com.example.raon.features.auth.ui.SignUpScreen
import com.example.raon.features.location.ui.LocationSearchScreen
import com.example.raon.features.splash.SplashScreen

/**
 * '인증 층'에 해당하는 네비게이션 그래프입니다.
 * 스플래시 화면부터 로그인/회원가입까지의 흐름을 관리합니다.
 */
fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(startDestination = "splash", route = "auth_graph") {

        // 첫 로딩 화면 -> 토큰 인증 확인 위해서 필요
        composable("splash") {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate("auth") {    // Auth 홈 화면으로 이동 -> 토큰 없음
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {     // Main 화면으로 이동 -> 토큰 있음
                    navController.navigate("main_graph") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            )
        }

        // Auth 홈 화면
        composable("auth") {
            AuthScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToLocationForSignup = { navController.navigate("location") }
            )
        }

        // Login 화면
        composable("login") {
            LoginScreen({
                navController.navigate("main_graph") {
                    popUpTo("auth") {
                        inclusive = true
                    }
                }
            })
        }


        // SginUp 하면
        composable(
            "signUp?locationId={locationId}&location={location}",
            arguments = listOf(
                navArgument("locationId") {
                    type = NavType.IntType
                    defaultValue = -1 // 기본값
                },
                navArgument("location") {
                    type = NavType.StringType
                    nullable = true // null 허용
                }
            )) {
            SignUpScreen(
                {
                    navController.navigate("main_graph") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
            )
        }


        // 위치 정하는 Screen
        composable("location") {
            LocationSearchScreen(
                onNavigateToSignup = { address, locationId ->
                    navController.navigate("signUp?locationId=${locationId}&location=${address}")
                },
                onBackClick = { // 뒤로 가기
                    navController.popBackStack()
                }
            )
        }

    }
}


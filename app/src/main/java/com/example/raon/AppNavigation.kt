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

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: KakaoAuthViewModel,
    context: Context,
) {

    // navController 생성
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {

        composable("auth") {
            AuthScreen(
                modifier,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToSignUp = { navController.navigate("signUp") },
                context
            )
        }

        composable("signUp") {
            SignUpScreen()
        }

        composable("login") {
            LoginScreen({ navController.navigate("main") })
        }

        composable("newAuthScreen") {
            AuthView(modifier, navController, viewModel, context)
        }




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
            SettingsScreen(navController)
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
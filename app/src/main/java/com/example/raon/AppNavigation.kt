package com.example.raon

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.raon.features.auth.ui.AuthView
import com.example.raon.features.auth.ui.KakaoAuthViewModel
import com.example.raon.features.bottom_navigation.b_test.ui.ViewModelTest
import com.example.raon.features.bottom_navigation.c_add_item.ui.AddItemScreen
import com.example.raon.features.bottom_navigation.d_chat.ui.ChatRoomScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: KakaoAuthViewModel,
    context: Context,
    viewModelTest: ViewModelTest
) {

    // navController 생성
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthView(modifier, navController, viewModel, context)
        }

        composable("main") {
            MainView(modifier, viewModelTest, navController)
        }

        composable("addItem") {
            AddItemScreen()
        }
        composable("chatroom") {
            ChatRoomScreen()
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
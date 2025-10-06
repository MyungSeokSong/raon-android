package com.example.raon

//import com.example.raon.features.item.ui.detail.ItemDetailScreen
//import com.example.raon.features.item.ui.detail.SellerInfo
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
import com.example.raon.features.bottom_navigation.d_chat.ui.ChatRoomScreen
import com.example.raon.features.item.ui.add.AddItemScreen
import com.example.raon.features.location.ui.LocationSearchScreen
import com.example.raon.features.search.ui.SearchInputScreen
import com.example.raon.features.search.ui.SearchResultScreen
import com.example.raon.features.user.ui.SettingsScreen

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

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ Item 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        composable("main") {
            MainView(modifier, navController)
        }


        composable("addItem") {
            AddItemScreen(
                modifier,
                onUploadSuccess = {
                    navController.navigate("itemDetail") {
                        // 현재 화면인 "addItem"을 백 스택에서 제거
                        popUpTo("addItem") {
                            inclusive = true
                        }
                    }
                },
                // onClose 파라미터에 뒤로 가기 동작을 전달
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        composable("itemDetail") {
//            val productitem = ProductItem(
//                id = 1,
//                title = "팝니다) 깨끗한 맥북 프로 14인치",
//                location = "서울시 강남구 역삼동",
//                timeAgo = "끌올 2분 전",
//                price = 1850000,
//                imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1926&q=80",
//                comments = 5,
//                likes = 23
//            )
//            val seller = SellerInfo("aaa", "seller", 36.5f, "주소")
//
//            ItemDetailScreen(productitem, seller, {})
        }


        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 바텀 네비게이션 화면 부분 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


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
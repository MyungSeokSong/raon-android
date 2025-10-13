package com.example.raon.features.main.ui

//import com.example.raon.features.item.ui.list.HomeScreen
//import com.example.raon.features.chat.ui.ChatListTopAppBar
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.raon.features.chat.ui.ChatListScreen
import com.example.raon.features.item.ui.list.HomeScreenTopAppBar
import com.example.raon.features.item.ui.list.ItemListScreen
import com.example.raon.features.user.ui.ProfileScreen
import com.example.raon.features.user.ui.ProfileTopAppBar
import com.example.raon.navigation.NavItem

// comp 자동 완성 키워드
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    modifier: Modifier = Modifier,
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()  // MainViewModel 실
) {

    // 👇 이 한 줄이 '구독'을 시작하게 만드는 핵심 코드입니다!
    // 이 코드가 실행되는 순간, ViewModel의 userProfile Flow가 활성화됩니다.
    val userProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()

//    val mainaddress = userProfile.address.split("").last()

    val mainaddress = userProfile?.address?.split(" ")?.lastOrNull()    // -> 풀 주소의 마지막 동만 가져오기


    val mainUiState by mainViewModel.uiState.collectAsState()
    // **핵심 변경 1: MainView 내부의 바텀 내비게이션 탭 관리를 위한 NavController 생성**
    val bottomNavController = rememberNavController()

    // 불변 List 자료구조 사용 - 굳이 수정될 이유가 없기 때문
    val navItemList = listOf(
        NavItem("홈", Icons.Default.Home, "home"),
        NavItem("찜", Icons.Default.HeartBroken, "chatRoomList"),
        NavItem("등록", Icons.Default.AddCircle, "addItem"),
        NavItem("채팅", Icons.Default.Send, "chatRoomList"),
        NavItem("프로필", Icons.Default.Person, "profile"),
    )

    // import getValue 해주기
    // 어떤 네비게이션 바를 선택했는지를 담은 변수
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // **핵심 변경 2: topBar는 bottomNavController의 현재 경로를 관찰하여 TopAppBar를 변경합니다.**
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            when (currentRoute) {
                "home" -> HomeScreenTopAppBar(
                    { navController.navigate("searchInput") {} },
                    address = mainaddress ?: "내 주소"
                )

//                "chat" -> ChatListTopAppBar()
                "profile" -> ProfileTopAppBar(navController)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier.height(115.dp)
            ) {
                // **핵심 변경 3: bottomNavController의 현재 경로를 관찰하여 선택된 탭을 강조합니다.**
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItemList.forEach { navItem ->
                    NavigationBarItem(
                        // 현재 navItem의 route와 bottomNavController의 currentRoute가 일치하면 선택됨

                        selected = currentRoute == navItem.route,
                        onClick = {

                            if (navItem.route == "addItem") {
                                // '등록' 탭을 눌렀을 때, 최상위 navController를 사용하여 새로운 화면으로 이동
                                navController.navigate("addItem")
                            } else {
                                // **핵심 변경 4: bottomNavController를 사용하여 해당 탭의 경로로 이동합니다.**
                                bottomNavController.navigate(navItem.route) {
                                    // 백 스택 관리 옵션:
                                    // 1. 시작 목적지까지 팝하여 백 스택에 여러 인스턴스가 쌓이는 것을 방지
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true // 현재 앱의 상태를 저장
                                    }
                                    // 2. 동일한 아이템을 다시 선택했을 때 새로운 목적지 인스턴스 생성 방지
                                    launchSingleTop = true
                                    // 3. 이전에 저장된 상태를 복원
                                    restoreState = true
                                }
                            }


//                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)

                            // ▼▼▼ 3. '채팅' 탭에 안 읽은 메시지 개수 배지 표시 ▼▼▼
//                            if (navItem.route == "chat") {
//                                BadgedBox(
//                                    badge = {
//                                        if (mainUiState.unreadChatCount > 0) {
//                                            Badge { Text(text = mainUiState.unreadChatCount.toString()) }
//                                        }
//                                    }
//                                ) {
//                                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
//                                }
//                            } else {
//                                Icon(imageVector = navItem.icon, contentDescription = navItem.label)
//                            }
                        }
                    )
                }
            }

        }

    ) { innerPadding ->
        // **핵심 변경 5: MainView 내부의 NavHost - 바텀 내비게이션 탭의 콘텐츠를 렌더링합니다.**

        NavHost(
            navController = bottomNavController,        // <-- 여기서는 bottomNavController를 사용합니다.
            startDestination = navItemList[0].route,    // MainView 진입 시 기본으로 보여줄 탭 (예: "home_tab")
            modifier = Modifier.padding(innerPadding)   // Scaffold가 제공하는 패딩을 적용하여 바텀바/탑바와 겹치지 않게 함
        ) {
            composable("home") {
                ItemListScreen(
                    // onNavigateToSearch: ItemListScreen 내부의 검색 아이콘을 눌렀을 때 실행될 동작
                    onNavigateToSearch = {
                        navController.navigate("searchScreen")  // searchScreen 경로로 이동
                    },
                    onItemClick = { itemId ->
                        navController.navigate("itemDetail/$itemId")
//                        navController.navigate("itemDetail")


                    }
                )
            }
            composable("chatRoomList") {
                ChatListScreen(
                    onChatRoomClick = { chatRoomId, sellerId ->
                        navController.navigate("chatRoom/$chatRoomId")

                        Log.d("채팅프로세스", "메인뷰 chatRoomId 전달 : ${chatRoomId}")
                    },
                    chatRooms = mainUiState.chatRooms // ◀◀ 이 부분이 핵심입니다.
                )
            }
            composable("profile") { ProfileScreen(navController) }
        }

    }
}


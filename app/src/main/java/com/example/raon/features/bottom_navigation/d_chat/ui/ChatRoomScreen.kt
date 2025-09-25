package com.example.raon.features.bottom_navigation.d_chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.raon.features.bottom_navigation.d_chat.ui.components.MessageBubble
import com.example.raon.features.bottom_navigation.d_chat.ui.components.MessageInput

// 채팅방 전체 UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("채팅방 이름") },

                )
        },
        bottomBar = {
            MessageInput(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        },
//        contentWindowInsets = WindowInsets.navigationBars   // battomBar와 안드로이드 시스템바와 안겹치게 하는 코드
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {

            LazyColumn {
                val messageArray: Array<String> = arrayOf(
                    "안녕하세요 게시글 보고 카톡드려요",
                    "네 안녕하세요~~",
                    "혹시 네고 가능할까요 이러이러해서 조금 싸게 하시는게 맞는거 같아서요",
                    "대신 제가 그쪽 지역까지 갈게요",
                    "꺼져",
                    "안녕하세요 게시글 보고 카톡드려요",
                    "네 안녕하세요~~",
                    "혹시 네고 가능할까요 이러이러해서 조금 싸게 하시는게 맞는거 같아서요",
                    "대신 제가 그쪽 지역까지 갈게요",
                    "꺼져",
                    "안녕하세요 게시글 보고 카톡드려요",
                    "네 안녕하세요~~",
                    "혹시 네고 가능할까요 이러이러해서 조금 싸게 하시는게 맞는거 같아서요",
                    "대신 제가 그쪽 지역까지 갈게요",
                    "꺼져",
                    "안녕하세요 게시글 보고 카톡드려요",
                    "네 안녕하세요~~",
                    "혹시 네고 가능할까요 이러이러해서 조금 싸게 하시는게 맞는거 같아서요",
                    "대신 제가 그쪽 지역까지 갈게요",
                    "꺼져"
                )
                val userArray: Array<Boolean> = arrayOf(
                    false,
                    true,
                    false,
                    false,
                    true,
                    false,
                    true,
                    false,
                    false,
                    true,
                    false,
                    true,
                    false,
                    false,
                    true,
                    false,
                    true,
                    false,
                    false,
                    true
                )



                items(20) { index ->
                    MessageBubble(modifier, messageArray[index], userArray[index])
                }

            }

        }

    }
}


@Preview
@Composable
fun ChatRoomScreenPreview(modifier: Modifier = Modifier) {
    ChatRoomScreen()
}
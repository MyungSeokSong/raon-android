package com.example.raon.features.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.raon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListTopAppBar() {
    TopAppBar(
        title = { Text("채팅") }
    )
}

@Composable
fun ChatListScreen(navController: NavController, modifier: Modifier = Modifier) {


    LazyColumn {
        items(5) { index ->
            ChatListItem(navController)
        }
    }

}

@Composable
fun ChatListItem(navController: NavController) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .clickable {
                navController.navigate("chatroom")
            }


    ) {
        Image(
            painter = painterResource(R.drawable.user_icon),
            contentDescription = "채팅 프로필",
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .size(50.dp)

        )

        Column {
            Text(
                text = "채팅방 이름", fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = "채팅 내용"
            )
        }


    }
}


package com.example.raon.features.bottom_navigation.a_home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.raon.R
import com.example.raon.testdata.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    TopAppBar(
        title = { Text("홈") }
    )
}

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {

    val viewMode = viewModel<ItemViewModel>()


    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 15.dp)
    ) {
        items(30) { index ->

            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable {
                        navController.navigate("chatroom")
                    }
            ) {
                Image(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(100.dp, 100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = painterResource(R.drawable.dog1),
                    contentDescription = "중고 거래 물품 이미지"
                )
                Column {
                    Text("중고 물품 팔아요", fontWeight = FontWeight.W500)
                    Text("설명 블라블라")
                    Text("10,000원", fontWeight = FontWeight.Bold)

//                    Text(viewMode.items.value.toString())
                }
            }

            HorizontalDivider(
                thickness = 1.dp
            )


        }
    }


}

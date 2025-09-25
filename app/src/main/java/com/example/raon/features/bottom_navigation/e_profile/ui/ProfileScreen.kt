package com.example.raon.features.bottom_navigation.e_profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raon.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar() {
    TopAppBar(
        title = { Text("프로필") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(bottom = 20.dp, top = 20.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .padding(start = 25.dp, end = 10.dp)
                    .size(60.dp)
            )
            Text(text = "닉네임", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

//        Spacer()

        Column {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_receipt),
                        contentDescription = "영수증 이미지",
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(20.dp)

                    )
                    Text("판매내역")
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.icon_right_arrow_96),
                        contentDescription = "오른쪽 화살표 이미지"
                    )

                }

            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_shopping_bag),
                        contentDescription = "영수증 이미지",
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(20.dp)

                    )
                    Text("구매내역")
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.icon_right_arrow_96),
                        contentDescription = "오른쪽 화살표 이미지"
                    )

                }

            }


        }
    }


}


@Preview
@Composable
fun ProfileScreenPreview(modifier: Modifier = Modifier) {
    ProfileScreen()
}
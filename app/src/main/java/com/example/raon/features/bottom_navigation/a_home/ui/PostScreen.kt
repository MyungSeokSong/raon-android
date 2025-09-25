package com.example.raon.features.bottom_navigation.a_home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.raon.R


@Composable
fun PostScreen(modifier: Modifier = Modifier) {

    // 이미지 데이터
    val imageResources = listOf(
        R.drawable.dog1,
        R.drawable.profile_image_cat,
        R.drawable.user_icon,
        R.drawable.oauth_image,
        R.drawable.dog1
    )

    Column(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(imageResources) { imageResId ->
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "포스트 이미지",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                )
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun PostScreenPreView(modifier: Modifier = Modifier) {
    PostScreen()
}




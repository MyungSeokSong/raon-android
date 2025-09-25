package com.example.raon.features.bottom_navigation.b_test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.raon.R
import com.example.raon.features.bottom_navigation.b_test.data.model.TestDTO

@Composable
fun Test(
    modifier: Modifier = Modifier,
    viewModelTest: ViewModelTest,
    viewModelTestHilt: ViewModelTestHilt = hiltViewModel()
) {

    val num: Int = viewModelTest.num.collectAsStateWithLifecycle().value
    val num2: Int = viewModelTestHilt.num.collectAsStateWithLifecycle().value

    val text1: List<TestDTO> = viewModelTestHilt.items.collectAsStateWithLifecycle().value



    Column {
        Text("테스트")
        Text("ViewModel 옵저버 패턴 테스트")
        Text("" + num)
        Text("" + num2)

        Text("서버 데이터 길이: " + text1.size)
//        Text("서버 데이터 꺼내기: "+text1[0].title)

        Text("서버 데이터 : " + text1)
    }

    LazyColumn {
        items(text1.size) { index ->

            Row(modifier = Modifier.padding(bottom = 10.dp)) {
                Image(
                    modifier = Modifier.size(100.dp, 100.dp),
                    painter = painterResource(R.drawable.dog1),
                    contentDescription = "중고 거래 물품 이미지"
                )
                Column {
                    Text(text1[index].title, fontWeight = FontWeight.W500)
                    Text(text1[index].description)
                    Text(text1[index].price.toString() + "원", fontWeight = FontWeight.Bold)

//                    Text(viewMode.items.value.toString())
                }
            }

            HorizontalDivider(
                thickness = 1.dp
            )


        }
    }

    Box(
        modifier = Modifier
            .size(200.dp) // Box의 크기 지정
            .background(androidx.compose.ui.graphics.Color.LightGray) // Box의 배경색 지정
    ) {

//        Image(
//            painter = painterResource(id = R.drawable.dog1),
//            contentDescription = "이미지"
//        )

        Column {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                contentDescription = "카메라"

            )

            // 첫 번째 자식 (기본적으로 좌측 상단에 배치)
            Text("Hello", modifier = Modifier.background(androidx.compose.ui.graphics.Color.Yellow))

            // 두 번째 자식 (첫 번째 자식 위에 겹쳐서 배치)
            Text("Compose", modifier = Modifier.background(androidx.compose.ui.graphics.Color.Cyan))
        }


    }


}
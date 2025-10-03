package com.example.raon.features.search.ui


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchAppBar(
    query: String, // 1. 표시할 검색어 (상태)
    onQueryChange: (String) -> Unit, // 2. 검색어가 변경될 때 호출될 함수 (이벤트)
    onSearch: () -> Unit, // 3. 키보드의 검색 버튼을 눌렀을 때 호출될 함수 (이벤트)
    onBackClick: () -> Unit, // 4. 뒤로가기 버튼을 눌렀을 때 호출될 함수 (이벤트)
    onHomeClick: () -> Unit, // 5. 홈 버튼을 눌렀을 때 호출될 함수 (이벤트)
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기 아이콘
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
        }

        // 검색어 입력 필드
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            placeholder = {
                Text(
                    "검색어를 입력해주세요",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )

        // 홈 아이콘
        IconButton(onClick = onHomeClick) {
            Icon(Icons.Default.Home, contentDescription = "홈")
        }
    }
}
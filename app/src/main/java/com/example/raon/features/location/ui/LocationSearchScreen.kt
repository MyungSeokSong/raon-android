package com.example.raon.features.location.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    onNavigateToSignup: (LocationInfo) -> Unit, // 회원가입 Screen 이동 람다
    onBackClick: () -> Unit, // 뒤로가기 람다
    locationViewModel: LocationViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val listUiState by locationViewModel.listUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                locationViewModel.fetchCurrentLocation()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("강남") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear Search")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // '현재 위치로 찾기' 버튼
            Button(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        locationViewModel.fetchCurrentLocation()
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp),
                // ✨ 요청하신 버튼 색상을 그대로 적용
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDCC31),
                    contentColor = Color(0xFF3C3C3C)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Current Location Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("현재 위치로 찾기", fontWeight = FontWeight.Bold)
                }
            }

            // 목록 상태에 따른 UI 분기
            when (val state = listUiState) {
                is LocationListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LocationListUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = state.locations) { locationInfo ->

                            LocationListItem(
                                locationInfo = locationInfo,
                                onItemClick = { selectedLocation ->

                                    onNavigateToSignup(selectedLocation)
                                }
                            )
                        }
                    }
                }

                is LocationListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message)
                    }
                }
            }
        }
    }
}

// 리스트 아이템을 그리는 별도의 Composable
@Composable
fun LocationListItem(
    locationInfo: LocationInfo,
    onItemClick: (LocationInfo) -> Unit // ✨ 3. NavController 대신 클릭 이벤트를 처리할 람다를 받습니다.
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(locationInfo) }    // Item들이 터치당하면 실행하는 람다 함수 넣어주기
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = locationInfo.mainAddress,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = locationInfo.subAddress,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            lineHeight = 16.sp,
            maxLines = 1 // 한 줄로 표시하고 나머지는 ... 처리
        )
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
}
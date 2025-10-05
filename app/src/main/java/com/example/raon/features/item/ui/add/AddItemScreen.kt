package com.example.raon.features.item.ui.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.raon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    modifier: Modifier = Modifier,
    onUploadSuccess: () -> Unit,
    onClose: () -> Unit = {},
    addItemViewModel: AddItemViewModel = hiltViewModel()
) {

    // ViewModel로부터 UI 상태를 구독
    val uiState by addItemViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSuccess) {    // 업로드 성공할 때
        onUploadSuccess()
    }

    // 갤러리 들어가는 코드
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->

        // 갤러리에서 선택한 이미지 uri을 전달 -> 이후 전달한 uri를 보여줌
        addItemViewModel.onEvent(AddItemEvent.AddImages(uris))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 물건 팔기", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ImageUploadSection(
                selectedImages = uiState.seletedImages,
                maxImageCount = 5,
                onAddImage = {  // 이미지 추가 버튼 눌렀을 때
                    pickMediaLauncher.launch(   // 갤러리 열기
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = { uri ->    // 이미지 삭제 버튼 눌렀을 때
                    addItemViewModel.onEvent(AddItemEvent.RemoveImage(uri))


                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ItemInfoSection(
                title = uiState.title,
                onTitleChange = { newTitle ->
                    addItemViewModel.onEvent(AddItemEvent.TitleChanged(newTitle))
                },
                description = uiState.description,
                onDescriptionChange = { newDescription ->
                    addItemViewModel.onEvent(AddItemEvent.DescriptionChanged(newDescription))
                },
                price = uiState.price.toString(),
                onPriceChange = { newPrice ->
                    addItemViewModel.onEvent(AddItemEvent.PriceChanged(newPrice))
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { // 서버에 상품 정보 등록 버튼
                    addItemViewModel.onEvent(AddItemEvent.Submit)
                },
                enabled = uiState.title.isNotBlank() && uiState.seletedImages.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(52.dp)
            ) {
                Text("등록 완료", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ImageUploadSection( // 이미지 업로드 부분 UI
    selectedImages: List<Uri>,
    maxImageCount: Int,
    onAddImage: () -> Unit,
    onRemoveImage: (Uri) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(onClick = onAddImage, enabled = selectedImages.size < maxImageCount),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = "이미지 추가",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${selectedImages.size}/$maxImageCount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        items(selectedImages) { imageUri ->
            Box(modifier = Modifier.size(80.dp)) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "선택된 이미지",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onRemoveImage(imageUri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "이미지 제거",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemInfoSection(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("제목") },
            placeholder = { Text("상품 제목을 입력해주세요.") },
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            label = { Text("설명") },
            placeholder = { Text("상품 설명을 자세하게 입력해주세요.\n(거래 장소, 상품 상태 등)") }
        )

        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("가격") },
            placeholder = { Text("가격을 입력해주세요.") },
            leadingIcon = { Text("₩", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}


package com.example.raon.features.item.ui.add

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // 👈 추가된 import
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.raon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    modifier: Modifier = Modifier,
    onUploadSuccess: () -> Unit,
    onNavigationToCategory: () -> Unit, // 카테고리 선택 Screen으로 이동
    onClose: () -> Unit = {},
    viewModel: AddItemViewModel = hiltViewModel(),
    onClearCategoryResult: () -> Unit // ✨ 나중에 카테고리를 초기화할 UI

) {
    // ✨ 이제 uiState는 여기서 직접 구독합니다.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSuccess) {
        onUploadSuccess()
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        viewModel.onEvent(AddItemEvent.AddImages(uris))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("내 물건 팔기", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onEvent(AddItemEvent.Submit) },
                enabled = uiState.title.isNotBlank() &&
                        uiState.isPriceValid &&           // 가격 유효성 검사
                        uiState.seletedImages.isNotEmpty() &&
                        uiState.isCategoryValid &&
                        uiState.description.isNotEmpty(),
//                        uiState.selectedCategory != null,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
            ) {
                Text("등록 완료", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ImageUploadSection(
                    selectedImages = uiState.seletedImages,
                    maxImageCount = 5,
                    onAddImage = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemoveImage = { uri ->
                        viewModel.onEvent(AddItemEvent.RemoveImage(uri))
                    }
                )
            }

            item {
                SectionHeader(title = "상품 정보")
            }

            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onEvent(AddItemEvent.TitleChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("상품명") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                )
            }

            item {
                CategorySelectionField(
//                    selectedCategoryName = uiState.selectedCategoryName,
                    selectedCategoryName = uiState.selectedCategoryName,

                    onClick = {
                        Log.d("CategorySelectionField", "카테고리 선택 클릭")
                        /* TODO: 카테고리 선택 화면 이동 */
                        onNavigationToCategory()
                    }
                )
            }

//            // 👇👇👇 [디자인 확인용] 상품 상태 선택 UI 추가된 부분 👇👇👇
//            item {
//                SectionHeader(title = "상품 상태")
//                ProductConditionSelector()
//            }
//            // 👆👆👆 여기까지 추가된 부분 👆👆👆

            // 👇👇👇 [핵심] 상품 상태 UI를 ViewModel과 연결하여 추가 👇👇👇
            item {
                SectionHeader(title = "상품 상태")
                ProductConditionSelector(
                    selectedCondition = uiState.productCondition,
                    onConditionSelected = { condition ->
                        viewModel.onEvent(AddItemEvent.ProductConditionChanged(condition))
                    }
                )
            }
            // 👆👆👆 여기까지 👆👆👆

            item {
                SectionHeader(title = "가격")
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.onEvent(AddItemEvent.PriceChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0") },
                    leadingIcon = { Text("₩") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                )
            }

            item {
                SectionHeader(title = "자세한 설명")
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onEvent(AddItemEvent.DescriptionChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = {
                        Text(
                            text = "브랜드, 모델명, 구매 시기, 하자 유무 등 상품 설명을 최대한 자세히 적어주세요.\n" +
                                    "개인정보(전화번호, SNS 계정 등)는 입력할 수 없어요."
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.description.length} / 2000",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ... (기존 CategorySelectionField, SectionHeader, ImageUploadSection 코드는 그대로 유지) ...

// [핵심 수정] 터치 상태에 따라 테두리 '두께'와 '색상'을 모두 변경하여 다른 필드와 완전히 동일하게 만듦
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelectionField(
    selectedCategoryName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // [변경] Material3에서 포커스된 OutlinedTextField의 기본 테두리 두께는 2.dp, 포커스 안되면 1.dp
    val borderThickness = if (isPressed) 2.dp else 1.dp
    // [변경] 터치(Pressed) 상태일 때는 다른 필드가 포커스된 색상과 동일하게, 아닐 때는 기본 색상으로 설정
    val borderColor = if (isPressed) {
        MaterialTheme.colorScheme.onSurfaceVariant   // 포커스 시 색상
    } else {
        MaterialTheme.colorScheme.outlineVariant     // 비포커스 시 색상
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(OutlinedTextFieldDefaults.MinHeight)
            .border(
                width = borderThickness, // 두께를 동적으로 적용
                color = borderColor,     // 색상을 동적으로 적용
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedCategoryName ?: "카테고리 선택",

//                text = selectedCategoryName ?: "카테고리 선택", // -> 수정 전
                color = if (selectedCategoryName != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//                contentDescription = "카테고리 선택",
//                tint = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
    }
}


// 👇👇👇 [디자인 확인용] 상품 상태 선택 UI Composable 추가 👇👇👇
//@Composable
//private fun ProductConditionSelector(modifier: Modifier = Modifier) {
//    Row(
//        modifier = modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        // '새 상품' 버튼 (선택된 상태로 미리보기)
//        Button(
//            onClick = { /* 로직 연결 전이라 비워둠 */ },
//            modifier = Modifier.weight(1f),
//            shape = RoundedCornerShape(12.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            ),
//            contentPadding = PaddingValues(vertical = 16.dp)
//        ) {
//            Text(text = "중고 상품")
//        }
//
//        // '중고 상품' 버튼 (선택 안 된 상태로 미리보기)
//        Button(
//            onClick = { /* 로직 연결 전이라 비워둠 */ },
//            modifier = Modifier.weight(1f),
//            shape = RoundedCornerShape(12.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
//            ),
//            contentPadding = PaddingValues(vertical = 16.dp)
//        ) {
//            Text(text = "새 상품")
//        }
//    }
//}


// 아래 Helper Composable들은 변경사항 없습니다.

@Composable
private fun SectionHeader(
    title: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ImageUploadSection(
    selectedImages: List<Uri>,
    maxImageCount: Int,
    onAddImage: () -> Unit,
    onRemoveImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    .clickable(onClick = onAddImage, enabled = selectedImages.size < maxImageCount),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = "이미지 추가",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${selectedImages.size}/$maxImageCount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        items(selectedImages) { imageUri ->
            Box(modifier = Modifier.size(88.dp)) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "선택된 이미지",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onRemoveImage(imageUri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(22.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "이미지 제거",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


// 👇👇👇 새로 추가된 Composable (상태를 받아 UI를 그림) 👇👇👇
@Composable
private fun ProductConditionSelector(
    selectedCondition: ProductCondition,
    onConditionSelected: (ProductCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductCondition.values().forEach { condition ->
            val isSelected = selectedCondition == condition
            Button(
                onClick = { onConditionSelected(condition) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = if (isSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(text = condition.displayName)
            }
        }
    }
}
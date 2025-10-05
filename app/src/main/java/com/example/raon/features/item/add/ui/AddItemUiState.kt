package com.example.raon.features.item.add.ui

import android.net.Uri

data class AddItemUiState(
    val title: String = "테스트 게시글 제목1",
    val description: String = "신발 사진들을 올리는 게시글이야",
    val price: Int = 30000,
    val seletedImages: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false // 등록 성공 여부 상태
)
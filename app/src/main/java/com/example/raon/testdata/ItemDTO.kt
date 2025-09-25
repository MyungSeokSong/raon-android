package com.example.raon.testdata

// 게시글 데이터를 담은 DTO
data class ItemDTO(
    val title: String,
    val price: Int,
    val description: String,
    val imageName: String,
    val user: String
)

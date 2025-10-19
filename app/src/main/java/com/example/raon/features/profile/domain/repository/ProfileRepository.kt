package com.example.raon.features.profile.domain.repository

import com.example.raon.core.ui.model.ItemListUiModel

// Domain Layer: 인터페이스 정의
interface ProfileRepository {

    // 내 판매 상품 Item들을 가져오는 함수
    suspend fun getMyProducts(): Result<List<ItemListUiModel>>


    // 찜 목록 조회 함수
    suspend fun getFavorites(): Result<List<ItemListUiModel>>

}
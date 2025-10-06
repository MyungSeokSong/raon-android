package com.example.raon.features.item.data.repository

import android.net.Uri
import com.example.raon.features.item.data.remote.dto.add.ItemResponse
import com.example.raon.features.item.data.remote.dto.list.ItemDto
import com.example.raon.features.item.ui.list.model.ItemUiModel

// 아이템 데이터 처리를 위한 인터페이스
interface ItemRepository {

    // --- Presigned URL을 포함한 UI 모델을 반환하는 새 함수 추가 ---
    suspend fun getItemsWithViewableUrls(page: Int): List<ItemUiModel>

    // 아이템 목록 조회. 실패 시 예외를 던짐
    suspend fun getItems(page: Int): List<ItemDto> // 이름 및 반환 타입 변경

    // 새 아이템 등록
    suspend fun postNewItem(
        title: String,
        description: String,
        price: Int,
        imageUris: List<Uri>
    ): ItemResponse
}
package com.example.raon.features.item.ui.add

import android.net.Uri

sealed class AddItemEvent {
    data class TitleChanged(val title: String) : AddItemEvent()

//    data class CategorySelected(val title: String) : AddItemEvent()

    // ✨ 아래 이벤트를 추가해주세요. ID와 이름을 모두 받습니다.
    data class CategorySelected(val id: Int, val name: String) : AddItemEvent() // 카테고리 변경 이벤트

    data class ProductConditionChanged(val condition: ProductCondition) :
        AddItemEvent()  // 상품 상태 변경 이벤트


    data class DescriptionChanged(val description: String) : AddItemEvent()
    data class PriceChanged(val price: String) : AddItemEvent()
    data class AddImages(val uris: List<Uri>) : AddItemEvent()
    data class RemoveImage(val uri: Uri) : AddItemEvent()
    object Submit : AddItemEvent()
}
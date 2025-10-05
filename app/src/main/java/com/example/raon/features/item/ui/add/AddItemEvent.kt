package com.example.raon.features.item.ui.add

import android.net.Uri

sealed class AddItemEvent {
    data class TitleChanged(val title: String) : AddItemEvent()
    data class DescriptionChanged(val description: String) : AddItemEvent()
    data class PriceChanged(val price: String) : AddItemEvent()
    data class AddImages(val uris: List<Uri>) : AddItemEvent()
    data class RemoveImage(val uri: Uri) : AddItemEvent()
    object Submit : AddItemEvent()
}
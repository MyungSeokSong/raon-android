package com.example.raon.features.item.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.ui.detail.model.ItemDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ------------------- UI Model -------------------
// 추천 경로: features/item/ui/detail/model/ItemDetailModel.kt

//data class ItemDetailModel(
//    val id: Int,
//    val imageUrls: List<String>,
//    val sellerNickname: String,
//    val sellerProfileUrl: String?,
//    val sellerAddress: String,
//    val title: String,
//    val category: String,
//    val createdAt: String,
//    val description: String,
//    val favoriteCount: Int,
//    val viewCount: Int,
//    val price: Int
//)

// ------------------- UI State -------------------
// 추천 경로: features/item/ui/detail/model/ItemDetailUiState.kt

data class ItemDetailUiState(
    val item: ItemDetailModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// ------------------- ViewModel -------------------

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle // 내비게이션 argument를 받기 위함
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // NavHost에 정의된 경로의 인자 이름("itemId")과 동일해야 합니다.
        val itemId: Int? = savedStateHandle["itemId"]

        if (itemId != null) {
            loadItemDetails(itemId)
        } else {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "상품 ID를 불러올 수 없습니다.")
            }
        }
    }

    private fun loadItemDetails(itemId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Repository에 특정 아이템의 상세 정보를 요청합니다.
                val itemDetails = itemRepository.getItemDetail(itemId)
                _uiState.update {
                    it.copy(isLoading = false, item = itemDetails)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "데이터를 불러오는 중 오류가 발생했습니다.")
                }
            }
        }
    }
}
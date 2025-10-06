package com.example.raon.features.item.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.ui.list.model.ItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemListUiState(
    val items: List<ItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0
)

@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // --- 바로 이 부분입니다! ---
                // Repository의 새 함수를 호출하여 Presigned URL까지 적용된 최종 UI 모델 목록을 바로 받습니다.
                val newItemsUiModel =
                    itemRepository.getItemsWithViewableUrls(page = _uiState.value.currentPage)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = it.items + newItemsUiModel,
                        currentPage = it.currentPage + 1
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "데이터를 불러오는데 실패했습니다.")
                }
            }
        }
    }

    // toUiModel, formatTimeAgo 함수는 이제 Repository에 있으므로 여기서 삭제합니다.
}
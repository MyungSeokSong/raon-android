package com.example.raon.features.item.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.ui.list.model.ItemUiModel
import com.example.raon.features.user.domain.repository.UserRepository
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
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository  // Datastore에서 Profile 주소 데이터 가져오기 위해서
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState = _uiState.asStateFlow()


    init {
        loadItems()

    }

    private fun loadItems() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {

            // 👇 2. 데이터 로딩을 '시작'하는 시점을 알려주는 로그

            Log.d(
                "ItemListViewModel",
                "Start loading items for page: ${_uiState.value.currentPage}"
            )

            _uiState.update { it.copy(isLoading = true) }
            try {
                // Repository의 새 함수를 호출하여 Presigned URL까지 적용된 최종 UI 모델 목록을 바로 받습니다.
                val newItemsUiModel =
                    itemRepository.getItemsWithViewableUrls(page = _uiState.value.currentPage)


                // 👇 3. Repository로부터 데이터를 '성공적으로 받아왔는지' 확인하는 가장 중요한 로그
                //    이 로그를 통해 실제로 어떤 데이터가 들어왔는지 확인할 수 있습니다.
                Log.d(
                    "ItemListViewModel",
                    "Successfully loaded ${newItemsUiModel.size} items: $newItemsUiModel"
                )

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
}
package com.example.raon.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.ui.model.ItemListUiModel
import com.example.raon.features.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI 상태를 나타내는 데이터 클래스 (이 부분은 변경 없음)
data class SalesHistoryUiState(
    val sellingItems: List<ItemListUiModel> = emptyList(),
    val reservedItems: List<ItemListUiModel> = emptyList(),
    val soldItems: List<ItemListUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SalesHistoryViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchMyProducts()
    }

    fun fetchMyProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            profileRepository.getMyProducts()
                .onSuccess { myItems ->
                    // 1. status 값으로 리스트를 그룹화합니다. (단 한 번의 순회)
                    val groupedByStatus = myItems.groupBy { it.status }

                    // 2. 미리 만들어 둔 그룹(Map)에서 각 상태에 맞는 리스트를 꺼내 씁니다.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // "AVAILABLE" 키로 그룹을 찾고, 없으면(null이면) 빈 리스트를 사용
                            sellingItems = groupedByStatus["AVAILABLE"] ?: emptyList(),
                            // "RESERVED" 키로 그룹을 찾고, 없으면 빈 리스트를 사용
                            reservedItems = groupedByStatus["RESERVED"] ?: emptyList(),
                            // "SOLD" 키로 그룹을 찾고, 없으면 빈 리스트를 사용
                            soldItems = groupedByStatus["SOLD"] ?: emptyList()
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }
}
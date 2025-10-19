package com.example.raon.features.profile.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.ui.model.ItemListUiModel
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI에 일회성으로 전달할 이벤트 (예: Toast 메시지)
 */
sealed class FavoritesEvent {
    data class ShowError(val message: String) : FavoritesEvent()
}

/**
 * FavoritesScreen의 UI 상태를 나타내는 데이터 클래스
 */
data class FavoritesUiState(
    val favoriteItems: List<ItemListUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val itemRepository: ItemRepository // 찜 상태 변경 API 호출을 위해 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<FavoritesEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchFavorites()
    }

    /**
     * 서버로부터 찜 목록을 가져오는 함수
     */
    fun fetchFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            profileRepository.getFavorites()
                .onSuccess { items ->
                    _uiState.update {
                        it.copy(isLoading = false, favoriteItems = items)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }

    /**
     * 'FavoritesScreen' 내에서 하트 아이콘을 클릭했을 때 호출되는 함수.
     * 낙관적 UI 업데이트를 사용하여 서버에 찜 상태 변경을 요청합니다.
     */
    fun toggleFavoriteStatus(itemId: Int) {
        viewModelScope.launch {
            val currentItem = _uiState.value.favoriteItems.find { it.id == itemId } ?: return@launch
            val newFavoriteState = !currentItem.isFavorite

            // 1. UI를 즉시 업데이트 (낙관적 업데이트)
            _uiState.update { currentState ->
                val updatedList = currentState.favoriteItems.map { item ->
                    if (item.id == itemId) item.copy(isFavorite = newFavoriteState) else item
                }
                currentState.copy(favoriteItems = updatedList)
            }

            // 2. 백그라운드에서 서버에 API 요청
            try {
                itemRepository.updateFavoriteStatus(itemId, newFavoriteState)
                Log.d(
                    "FavoritesViewModel",
                    "찜 상태 변경 성공: itemId=$itemId, isFavorite=$newFavoriteState"
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                // 3. 실패 시 UI 롤백 및 에러 이벤트 발생
                _uiState.update { currentState ->
                    val rolledBackList = currentState.favoriteItems.map { item ->
                        if (item.id == itemId) item.copy(isFavorite = currentItem.isFavorite) else item
                    }
                    currentState.copy(favoriteItems = rolledBackList)
                }
                _eventFlow.emit(FavoritesEvent.ShowError("찜 상태 변경에 실패했습니다."))
                Log.e("FavoritesViewModel", "찜 상태 변경 실패: itemId=$itemId", e)
            }
        }
    }

    /**
     * 'ItemDetailScreen'에서 뒤로가기 시 전달된 결과를 받아 목록 상태를 업데이트하는 함수
     */
    fun updateFavoriteStatusFromResult(itemId: Int, isFavorite: Boolean) {
        // 관심 목록에서는 찜이 취소되면(isFavorite = false) 목록에서 해당 아이템을 제거합니다.
        if (!isFavorite) {
            _uiState.update { currentState ->
                val updatedList = currentState.favoriteItems.filterNot { it.id == itemId }
                currentState.copy(favoriteItems = updatedList)
            }
        }
        // isFavorite가 true인 경우는 이미 목록에 있으므로 별도로 처리할 필요가 없습니다.
    }
}
// features/profile/ui/FavoritesViewModel.kt

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

// 일회성 이벤트를 위한 Sealed Class
sealed class FavoritesEvent {
    data class ShowError(val message: String) : FavoritesEvent()
}

// UI 상태 데이터 클래스
data class FavoritesUiState(
    val favoriteItems: List<ItemListUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val itemRepository: ItemRepository // 찜 상태 변경을 위해 ItemRepository 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    // 이벤트 처리를 위한 SharedFlow
    private val _eventFlow = MutableSharedFlow<FavoritesEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchFavorites()
    }

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
     * UI에서 하트 아이콘을 클릭했을 때 호출되는 함수
     */
    fun toggleFavoriteStatus(itemId: Int) {
        viewModelScope.launch {
            // 1. 현재 상태에서 클릭된 아이템을 찾습니다.
            val currentItem = _uiState.value.favoriteItems.find { it.id == itemId } ?: return@launch
            val newFavoriteState = !currentItem.isFavorite

            // 2. '낙관적 UI 업데이트': 서버 응답을 기다리지 않고 UI를 즉시 변경합니다.
            _uiState.update { currentState ->
                val updatedList = currentState.favoriteItems.map { item ->
                    if (item.id == itemId) item.copy(isFavorite = newFavoriteState) else item
                }
                currentState.copy(favoriteItems = updatedList)
            }

            // 3. 백그라운드에서 서버에 API를 요청합니다.
            try {
                itemRepository.updateFavoriteStatus(itemId, newFavoriteState)
                // 성공! UI는 이미 변경되었으므로 아무것도 하지 않습니다.
                Log.d(
                    "FavoritesViewModel",
                    "찜 상태 변경 성공: itemId=$itemId, isFavorite=$newFavoriteState"
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                // 4. 실패! UI를 원래 상태로 되돌리고(롤백), 사용자에게 에러 메시지를 보냅니다.
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
}
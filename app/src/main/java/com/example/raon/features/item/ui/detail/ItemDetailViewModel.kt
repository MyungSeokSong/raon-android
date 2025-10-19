package com.example.raon.features.item.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.ui.detail.model.ItemDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

// ------------------- UI State -------------------
data class ItemDetailUiState(
    val item: ItemDetailModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// ------------------- UI Event -------------------
sealed class ItemDetailEvent {
    data class NavigateToChatRoom(val chatId: Long) : ItemDetailEvent()
    data class ShowError(val message: String) : ItemDetailEvent()
    object ProductDeleted : ItemDetailEvent()
    object ShowProductNotFoundError : ItemDetailEvent()
}

// ------------------- ViewModel -------------------
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ItemDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val itemId: Int? = savedStateHandle["itemId"]

    init {
        if (itemId != null) {
            loadItemDetails(itemId)
            increaseViewCount(itemId)
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
                // 1. 상품 상세 정보와 찜 상태를 '동시에' 비동기로 요청합니다.
                val detailDeferred = async { itemRepository.getItemDetail(itemId) }
                val favoriteStatusDeferred = async { itemRepository.getFavoriteStatus(itemId) }

                // 2. 두 요청이 모두 끝날 때까지 기다립니다.
                val itemDetails = detailDeferred.await()
                val isFavorite = favoriteStatusDeferred.await()

                // 3. 두 결과를 합쳐서 최종 UI 모델을 만듭니다.
                val finalItemDetails = itemDetails.copy(
                    isFavorite = isFavorite, // 찜 상태 API 결과를 모델에 반영
                    viewCount = itemDetails.viewCount + 1 // 조회수 1 증가시켜서 보여주기
                )

                // 4. 합쳐진 데이터로 UI 상태를 업데이트합니다.
                _uiState.update {
                    it.copy(isLoading = false, item = finalItemDetails)
                }

            } catch (e: Exception) {
                val errorMessage = if (e is HttpException && e.code() == 404) {
                    _eventFlow.emit(ItemDetailEvent.ShowProductNotFoundError)
                    _uiState.update { it.copy(isLoading = false) }
                    "404 에러 존재하지 않는 상품이거나 삭제되었습니다."
                } else {
                    "데이터를 불러오는 중 오류가 발생했습니다."
                }
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = errorMessage)
                }
            }
        }
    }

    fun deleteProduct() {
        if (itemId == null) {
            viewModelScope.launch { _eventFlow.emit(ItemDetailEvent.ShowError("상품 ID가 없어 삭제할 수 없습니다.")) }
            return
        }

        viewModelScope.launch {
            when (val result = itemRepository.deleteProduct(itemId)) {
                is ApiResult.Success -> {
                    _eventFlow.emit(ItemDetailEvent.ProductDeleted)
                }

                is ApiResult.Error -> {
                    val errorMessage = result.errorBody?.message ?: "삭제 중 오류가 발생했습니다."
                    _eventFlow.emit(ItemDetailEvent.ShowError(errorMessage))
                }

                is ApiResult.Exception -> {
                    _eventFlow.emit(
                        ItemDetailEvent.ShowError(
                            result.e.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    )
                }
            }
        }
    }

    fun onChatButtonClicked() {
        if (itemId == null) {
            viewModelScope.launch { _eventFlow.emit(ItemDetailEvent.ShowError("상품 ID가 없어 채팅을 시작할 수 없습니다.")) }
            return
        }

        viewModelScope.launch {
            when (val result = itemRepository.getChatRoomForItem(itemId.toLong())) {
                is ApiResult.Success -> {
                    _eventFlow.emit(ItemDetailEvent.NavigateToChatRoom(result.data.data.chatId))
                }

                is ApiResult.Error -> {
                    if (result.code == 404 && result.errorBody?.code == "CHAT_404") {
                        createChatRoom(itemId.toLong())
                    } else {
                        val errorMessage = result.errorBody?.message ?: "에러 코드: ${result.code}"
                        _eventFlow.emit(ItemDetailEvent.ShowError(errorMessage))
                    }
                }

                is ApiResult.Exception -> {
                    _eventFlow.emit(ItemDetailEvent.ShowError(result.e.message ?: "알 수 없는 오류"))
                }
            }
        }
    }

    private suspend fun createChatRoom(itemId: Long) {
        when (val result = itemRepository.createChatForItem(itemId)) {
            is ApiResult.Success -> {
                _eventFlow.emit(ItemDetailEvent.NavigateToChatRoom(result.data.data.chatId))
            }

            is ApiResult.Error -> {
                _eventFlow.emit(ItemDetailEvent.ShowError("채팅방 생성 실패: ${result.code}"))
            }

            is ApiResult.Exception -> {
                _eventFlow.emit(ItemDetailEvent.ShowError(result.e.message ?: "알 수 없는 오류"))
            }
        }
    }

    private fun increaseViewCount(itemId: Int) {
        viewModelScope.launch {
            itemRepository.increaseViewCount(itemId)
        }
    }

    fun onFavoriteButtonClicked() {
        val currentItem = _uiState.value.item ?: return

        val newFavoriteState = !currentItem.isFavorite
        val newFavoriteCount =
            if (newFavoriteState) currentItem.favoriteCount + 1 else currentItem.favoriteCount - 1

        _uiState.update {
            it.copy(
                item = currentItem.copy(
                    isFavorite = newFavoriteState,
                    favoriteCount = newFavoriteCount
                )
            )
        }

        viewModelScope.launch {
            try {
                itemRepository.updateFavoriteStatus(currentItem.id, newFavoriteState)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(item = currentItem)
                }
                _eventFlow.emit(ItemDetailEvent.ShowError("찜 상태 변경에 실패했습니다."))
            }
        }
    }
}
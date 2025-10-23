package com.example.raon.features.item.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.common.AppConstants
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.repository.ImageStorageRepository
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
    val errorMessage: String? = null,
    val viewableSellerProfileImageUrl: String? = null
)

// ------------------- UI Event -------------------
sealed class ItemDetailEvent {
    data class NavigateToChatRoom(val chatId: Long) : ItemDetailEvent()
    data class ShowError(val message: String) : ItemDetailEvent()
    object ProductDeleted : ItemDetailEvent()
    object ShowProductNotFoundError : ItemDetailEvent()
    object Refresh : ItemDetailEvent() // "새로고침" 이벤트 추가
}

// ------------------- ViewModel -------------------
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val imageStorageRepository: ImageStorageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ItemDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // ViewModel이 생성될 때 한 번만 저장되는 멤버 변수
    private val itemId: Int? = savedStateHandle["itemId"]

    init {
        if (itemId != null) {
            reloadData() // ViewModel 생성 시 데이터를 로드합니다.
            increaseViewCount(itemId)
        } else {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "상품 ID를 불러올 수 없습니다.")
            }
        }
    }

    /**
     * UI 레이어로부터 이벤트를 받는 유일한 public 함수입니다.
     */
    fun onEvent(event: ItemDetailEvent) {
        when (event) {
            ItemDetailEvent.Refresh -> reloadData()

            // 👇 빠진 4가지 경우를 모두 추가해줍니다.
            // 이 이벤트들은 ViewModel -> UI 방향이므로 onEvent에서는 할 일이 없습니다.
            is ItemDetailEvent.NavigateToChatRoom -> { /* Do nothing */
            }

            is ItemDetailEvent.ProductDeleted -> { /* Do nothing */
            }

            is ItemDetailEvent.ShowError -> { /* Do nothing */
            }

            is ItemDetailEvent.ShowProductNotFoundError -> { /* Do nothing */
            }
        }
    }

    /**
     * 데이터를 다시 로드하는 private 함수. ViewModel 내부에서만 호출됩니다.
     * 파라미터가 필요 없는 이유는 클래스의 멤버 변수인 `itemId`를 사용하기 때문입니다.
     */
    private fun reloadData() {
        val currentItemId = itemId ?: return // 멤버 변수 itemId를 사용합니다.

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 상품 상세 정보와 찜 상태를 '동시에' 비동기로 요청합니다.
                val detailDeferred = async { itemRepository.getItemDetail(currentItemId) }
                val favoriteStatusDeferred =
                    async { itemRepository.getFavoriteStatus(currentItemId) }

                // 두 요청이 모두 끝날 때까지 기다립니다.
                val itemDetails = detailDeferred.await()
                val isFavorite = favoriteStatusDeferred.await()

                // 판매자 프로필 Presigned URL 요청 로직
                var sellerPresignedUrl: String? = null
                val sellerS3Key =
                    itemDetails.sellerProfileUrl?.removePrefix(AppConstants.S3_BASE_URL)

                if (sellerS3Key != null && itemDetails.sellerProfileUrl != AppConstants.DEFAULT_PROFILE_URL) {
                    try {
                        sellerPresignedUrl =
                            imageStorageRepository.getPresignedImageUrl(sellerS3Key).getOrNull()
                        Log.d(
                            "ItemDetailViewModel",
                            "✅ Seller Presigned URL loaded: $sellerPresignedUrl"
                        )
                    } catch (e: Exception) {
                        Log.e("ItemDetailViewModel", "❌ Failed to load seller Presigned URL", e)
                    }
                }

                // 두 결과를 합쳐서 최종 UI 모델을 만듭니다.
                val finalItemDetails = itemDetails.copy(
                    isFavorite = isFavorite, // 찜 상태 API 결과를 모델에 반영
                    viewCount = itemDetails.viewCount + 1 // 조회수 1 증가시켜서 보여주기
                )

                // 합쳐진 데이터로 UI 상태를 업데이트합니다.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        item = finalItemDetails,
                        viewableSellerProfileImageUrl = sellerPresignedUrl
                    )
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
                        ItemDetailEvent.ShowError(result.e.message ?: "알 수 없는 오류가 발생했습니다.")
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
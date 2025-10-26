package com.example.raon.features.profile.ui

import android.util.Log // ▼▼▼ [추가] Log import ▼▼▼
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.item.data.remote.dto.update_status.BuyerDto
import com.example.raon.features.item.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BuyerUiModel(val id: Int, val nickname: String, val profileImageUrl: String?)

data class BuyerSelectionUiState(
    val buyers: List<BuyerUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class BuyerSelectionViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Int = savedStateHandle.get<Int>("itemId") ?: -1

    private val _uiState = MutableStateFlow(BuyerSelectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchBuyers()
    }

    private fun fetchBuyers() {
        // ▼▼▼ [로그 추가 1] 함수 시작 시 어떤 itemId로 요청하는지 확인 ▼▼▼
        Log.d("BuyerSelectionVM", "Attempting to fetch buyers for itemId: $itemId")

        if (itemId == -1) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "상품 정보를 가져올 수 없습니다.") }
            // ▼▼▼ [로그 추가 2] 유효하지 않은 itemId일 경우 에러 로그 출력 ▼▼▼
            Log.e("BuyerSelectionVM", "Invalid itemId (-1). Cannot fetch buyers.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = itemRepository.getBuyersForProduct(itemId)

            // ▼▼▼ [로그 추가 3] API 호출 직후, 결과가 성공인지 실패인지 확인 ▼▼▼
            Log.d("BuyerSelectionVM", "API Result received: $result")

            when (result) {
                is ApiResult.Success -> {
                    val buyerUiModels = result.data.data.users.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            buyers = buyerUiModels
                        )
                    }
                    // ▼▼▼ [로그 추가 4] 성공 시, 몇 명의 구매자 정보를 받았는지 확인 ▼▼▼
                    Log.i("BuyerSelectionVM", "Successfully fetched ${buyerUiModels.size} buyers.")
                }

                is ApiResult.Error -> {
                    val errorMessage = result.errorBody?.message ?: "구매자 목록을 불러오는데 실패했습니다."
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                    // ▼▼▼ [로그 추가 5] 서버 에러 시, 코드와 메시지 확인 (Error 레벨) ▼▼▼
                    Log.e(
                        "BuyerSelectionVM",
                        "API Error occurred. Code: ${result.code}, Message: $errorMessage"
                    )
                }

                is ApiResult.Exception -> {
                    val exceptionMessage = result.e.message ?: "네트워크 오류가 발생했습니다."
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exceptionMessage
                        )
                    }
                    // ▼▼▼ [로그 추가 6] 네트워크 등 예외 발생 시, 예외 메시지 확인 (Error 레벨) ▼▼▼
                    Log.e(
                        "BuyerSelectionVM",
                        "Network or other exception occurred: $exceptionMessage"
                    )
                }
            }
        }
    }
}

private fun BuyerDto.toUiModel(): BuyerUiModel {
    return BuyerUiModel(
        id = this.userId,
        nickname = this.nickname,
        profileImageUrl = this.profileImage
    )
}
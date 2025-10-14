package com.example.raon.features.item.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.ui.detail.model.ItemDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ------------------- UI State -------------------
// 이 객체로 UI의 상태를 알 수 있음
data class ItemDetailUiState(
    val item: ItemDetailModel? = null,  // 가져온 상품 데이터
    val isLoading: Boolean = true,      // 로딩중인지
    val errorMessage: String? = null    // 데이터 불러오기 실패, 에러
)

// ------------------- UI Event -------------------
// 채팅방 이동, 오류 메시지 등 일회성 이벤트를 처리하기 위한 Sealed Class
sealed class ItemChatEvent {
    data class NavigateToChatRoom(val chatId: Long) : ItemChatEvent()
    data class ShowError(val message: String) : ItemChatEvent()
}


// ------------------- ViewModel -------------------
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle // 내비게이션 argument를 받기 위함
) : ViewModel() {

    // 화면의 '상태'를 관리 (로딩, 아이템 정보 등)
    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    // 화면 이동 등 '이벤트'를 관리
    private val _eventFlow = MutableSharedFlow<ItemChatEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // ViewModel 전체에서 사용할 수 있도록 itemId를 멤버 변수로 저장
    private val itemId: Int? = savedStateHandle["itemId"]

    init {
        // NavHost에 정의된 경로의 인자 이름("itemId")과 동일해야 합니다.
        if (itemId != null) {

            // 보여줄 데이터 가져오기
            loadItemDetails(itemId)

            // 조회수 증가 요청
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
                // Repository에 특정 아이템의 상세 정보를 요청합니다.
                val itemDetails = itemRepository.getItemDetail(itemId)

                // 사용자에게 미리 조회수 1을 증가시켜서 보여주기 -> 서버로는 별도로 요청
                val updateItemDetails = itemDetails.copy(
                    viewCount = itemDetails.viewCount + 1
                )

                _uiState.update {
                    it.copy(isLoading = false, item = updateItemDetails)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "데이터를 불러오는 중 오류가 발생했습니다.")
                }
            }
        }
    }

    /**
     * UI에서 '채팅하기' 버튼을 눌렀을 때 호출되는 함수
     */
    fun onChatButtonClicked() {
        if (itemId == null) {
            viewModelScope.launch {
                _eventFlow.emit(ItemChatEvent.ShowError("상품 ID가 없어 채팅을 시작할 수 없습니다."))
            }
            return
        }

        viewModelScope.launch {
            // 1. 기존 채팅방이 있는지 확인
            when (val result = itemRepository.getChatRoomForItem(itemId.toLong())) {
                is ApiResult.Success -> {
                    // 성공 시 -> 바로 채팅방으로 이동 이벤트 발생
                    _eventFlow.emit(ItemChatEvent.NavigateToChatRoom(result.data.data.chatId))

                    Log.d(
                        "ItemDetailViewModel",
                        "채팅방이 이미 존재합니다. chatId: ${result.data.data.chatId}"
                    )

                }

                is ApiResult.Error -> {
                    if (result.code == 404 && result.errorBody?.code == "CHAT_404") {

                        Log.d("ItemDetailViewModel", "채팅방 없음")

                        // 채팅방이 없으면(404 + CHAT_404 메시지) -> 채팅방 생성 시도
                        createChatRoom(itemId.toLong())

                    } else {

                        // 그 외 모든 에러 (상품 없음(PROD_404), 서버 에러 등)는 사용자에게 알림
                        val errorMessage = result.errorBody?.message ?: "에러 코드: ${result.code}"
                        _eventFlow.emit(ItemChatEvent.ShowError(errorMessage))

                        Log.d("ItemDetailViewModel", "에러 발생: $errorMessage")

//                        _eventFlow.emit(ItemChatEvent.ShowError("에러 코드: ${result.code}"))
                    }
                }

                is ApiResult.Exception -> {
                    _eventFlow.emit(ItemChatEvent.ShowError(result.e.message ?: "알 수 없는 오류"))
                }
            }
        }
    }

    /**
     * 채팅방이 없을 경우, 새로운 채팅방을 생성하도록 서버에 요청하는 함수
     */
    private suspend fun createChatRoom(itemId: Long) {
        when (val result = itemRepository.createChatForItem(itemId)) {
            is ApiResult.Success -> {
                // 생성 성공 시 -> 생성된 chatId로 채팅방 이동 이벤트 발생
                _eventFlow.emit(ItemChatEvent.NavigateToChatRoom(result.data.data.chatId))
                Log.d("ItemDetailViewModel", "채팅방 생성 성공")

            }

            is ApiResult.Error -> {
                _eventFlow.emit(ItemChatEvent.ShowError("채팅방 생성 실패: ${result.code}"))
                Log.d("ItemDetailViewModel", "채팅방 생성 에러 발생: ${result.code}")

            }

            is ApiResult.Exception -> {
                _eventFlow.emit(ItemChatEvent.ShowError(result.e.message ?: "알 수 없는 오류"))
                Log.d("ItemDetailViewModel", "채팅방 생성 Exception 발생: ${result.e.message}")

            }
        }
    }


    /**
     *  상품 조회수를 1 증가시키는 함수
     */
    private fun increaseViewCount(itemId: Int) {
        // UI 상태를 변경하지 않고, 백그라운드에서 조용히 API만 호출합니다.
        viewModelScope.launch {
            itemRepository.increaseViewCount(itemId)
        }
    }
}
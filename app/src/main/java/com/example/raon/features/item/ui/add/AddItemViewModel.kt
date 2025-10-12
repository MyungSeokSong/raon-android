package com.example.raon.features.item.ui.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.item.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AddItemEvent) {
        when (event) {
            is AddItemEvent.TitleChanged -> {   // 제목이 바뀌었을 때 새 객체 만들어서 업데이트
                _uiState.update { it.copy(title = event.title) }
            }

            is AddItemEvent.DescriptionChanged -> { // description이 바뀌었을 때 새 객체 만들어서 업데이트
                _uiState.update { it.copy(description = event.description) }
            }

            is AddItemEvent.PriceChanged -> {   // 가격이 바뀌었을 때 새 객체 만들어서 업데이트
                // 숫자만 입력되도록 필터링하는 로직 등을 추가할 수 있음
                _uiState.update { it.copy(price = event.price.toIntOrNull() ?: 0) }
            }

            is AddItemEvent.AddImages -> {  // 이미지가 추가됬을 때
                _uiState.update {
                    val currentImages = it.seletedImages.toMutableList()
                    currentImages.addAll(event.uris)
                    it.copy(seletedImages = currentImages.take(5)) // 최대 5개 보장
                }
            }

            is AddItemEvent.RemoveImage -> {    // 이미지가 삭제 됬을 때
                _uiState.update {
                    val currentImages = it.seletedImages.toMutableList()
                    currentImages.remove(event.uri)
                    it.copy(seletedImages = currentImages)
                }
            }

            is AddItemEvent.Submit -> postItem()    // item 등록 완료 버튼 이벤트
        }
    }


    private fun postItem() {
        viewModelScope.launch {

            Log.d("addItem", "postItem 함수")

            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentState = _uiState.value
                val response = itemRepository.postNewItem(  // 서버 통신 코드
                    title = currentState.title,
                    description = currentState.description,
                    price = currentState.price,
                    imageUris = currentState.seletedImages
                )

                Log.d("addItem", "local imageUris: {${currentState.seletedImages}}")
                Log.d("addItem", "response: {${response.code}}")
                Log.d("addItem", "response: {${response.message}}")


                // 서버 통신에 따른 처리
                if (response.code == "OK") {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    // TODO: 성공 시 화면 이동 (예: NavController.navigate)
                } else {
                    // TODO: 서버가 보낸 에러 메시지 처리
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                // TODO: 네트워크 에러 등 예외 처리
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
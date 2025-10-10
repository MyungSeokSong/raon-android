package com.example.raon.features.main.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.network.ApiResult
import com.example.raon.features.chat.data.remote.dto.ChatRoomInfo
import com.example.raon.features.chat.domain.repository.ChatRepository
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // 👈 Flow에서 첫 번째 값을 꺼내기 위해 import
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val chatRooms: List<ChatRoomInfo> = emptyList(),
    val unreadChatCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val chatRoomsJob = async { chatRepository.getChatRoomList(page = 0) }
            val userProfileJob = async { userRepository.fetchAndSaveUserProfile() }

            val chatResult = chatRoomsJob.await()
            val profileResult = userProfileJob.await()

            // ▼▼▼ 이 부분이 핵심입니다 ▼▼▼
            // 1. 프로필 저장 작업이 성공했는지 확인합니다.
            if (profileResult is ApiResult.Success) {
                // 2. UserRepository의 getUserProfile() Flow에서 첫 번째 값을 꺼내옵니다.
                //    이것이 바로 DataStore에 저장된 최신 데이터입니다.
                val savedUser = userRepository.getUserProfile().first()

                // 3. 꺼내온 실제 데이터를 로그로 출력합니다.
                Log.d("MainViewModel", "✅ DataStore 저장 데이터 확인: $savedUser")
            } else {
                Log.e("MainViewModel", "❌ 사용자 프로필 가져오기/저장 실패")
            }

            // 채팅 목록 상태 업데이트 (기존과 동일)
            if (chatResult is ApiResult.Success) {
                val chatList = chatResult.data.data?.chats ?: emptyList()
                val unreadCount = chatList.sumOf { it.unreadCount }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        chatRooms = chatList,
                        unreadChatCount = unreadCount
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
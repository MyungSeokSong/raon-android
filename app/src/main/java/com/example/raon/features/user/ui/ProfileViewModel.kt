package com.example.raon.features.user.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.user.domain.model.User
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // DataStore의 userProfileFlow를 관찰하여 UI에 제공할 StateFlow로 변환
    val userProfile: StateFlow<User?> = userRepository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            // 앱이 백그라운드로 가도 5초간 구독을 유지하여, 다시 돌아왔을 때 데이터를 새로 로딩하는 것을 방지
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // 초기값은 null
        )

    init {
        // ViewModel이 생성될 때, 최신 프로필 정보를 서버에서 가져오도록 요청
        // 이 데이터는 DataStore에 저장되고, 위의 userProfile Flow가 자동으로 감지하여 UI를 업데이트
        viewModelScope.launch {
            userRepository.fetchAndSaveUserProfile()
        }
    }
}
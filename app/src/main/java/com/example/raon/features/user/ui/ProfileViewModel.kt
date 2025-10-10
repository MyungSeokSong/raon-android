package com.example.raon.features.user.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.user.domain.model.User
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // DataStore의 userProfileFlow를 관찰하여 UI에 제공할 StateFlow로 변환
    val userProfile: StateFlow<User?> = userRepository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 데이터 로딩 로직이 MainViewModel로 이전되었으므로 init 블록을 주석.
    // init {
    //     viewModelScope.launch {
    //         userRepository.fetchAndSaveUserProfile()
    //     }
    // }
}
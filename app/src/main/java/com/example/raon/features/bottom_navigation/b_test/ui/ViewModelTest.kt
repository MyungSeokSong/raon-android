package com.example.raon.features.bottom_navigation.b_test.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelTest : ViewModel() {

    private val _data = MutableStateFlow("Test Data")
    val data: StateFlow<String> = _data

    private val _num = MutableStateFlow(0)    // flow 사용
    val num: StateFlow<Int> = _num.asStateFlow()

    init {
        viewModelScope.launch { // 코루틴 사용
            var count = 0
            while (true) {
                delay(1000L) // 1초 대기
                _num.value = ++count // 값 업데이트 (Flow가 새로운 값 방출)

            }
        }
    }


}
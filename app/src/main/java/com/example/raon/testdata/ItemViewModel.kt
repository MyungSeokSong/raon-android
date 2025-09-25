package com.example.raon.testdata

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ItemViewModel : ViewModel() {

    // 내부적으로 값을 변경할 수 있는 MutableStateFlow 선언
    private val _count = MutableStateFlow(0)

    // 외부에 읽기 전용 StateFlow로 노출
    val count: StateFlow<Int> = _count.asStateFlow()


}
package com.example.raon.features.bottom_navigation.b_test.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.bottom_navigation.b_test.data.model.TestDTO
import com.example.raon.features.bottom_navigation.b_test.data.repository.RepositoryTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTestHilt @Inject constructor(private val repositoryTest: RepositoryTest) :
    ViewModel() {
    private val _data = MutableStateFlow("Test Data")
    val data: StateFlow<String> = _data

    private val _num = MutableStateFlow(0)    // flow 사용
    val num: StateFlow<Int> = _num.asStateFlow()

    private val _items = MutableStateFlow<List<TestDTO>>(emptyList())
    val items: StateFlow<List<TestDTO>> = _items


    init {
        viewModelScope.launch { // 코루틴 사용


            Log.e("서버데이터 ready", "준비중")

            val items_test = repositoryTest.getItem()
            Log.e("서버데이터 start", items_test.toString())

            _items.value = items_test.body()!!

            Log.e("서버데이터 end", items_test.toString())


            var count = 0
            while (true) {
                delay(300L) // 0.3초 대기
                _num.value = ++count // 값 업데이트 (Flow가 새로운 값 방출)
            }

//            val items_test = retrofitApiTest.getItems()


        }
    }
}
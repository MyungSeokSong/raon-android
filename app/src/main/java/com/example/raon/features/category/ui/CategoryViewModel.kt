package com.example.raon.features.category.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.category.data.local.CategoryEntity
import com.example.raon.features.category.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val argParentId: Int = savedStateHandle.get<Int>("parentId") ?: -1
    private val parentId: Int? = if (argParentId == -1) null else argParentId

    // ✨ 1. 화면 이동 시 전달받은 경로 문자열 (예: "여성의류,상의")
    private val pathString: String? = savedStateHandle.get<String>("path")

    val pathString2 = pathString

    // ✨ 2. UI에 표시할 최종 경로 리스트 StateFlow -> 경로 스트링 리스트
    private val _categoryPath = MutableStateFlow<List<String>>(emptyList())
    val categoryPath: StateFlow<List<String>> = _categoryPath.asStateFlow()


    init {
        Log.d("CategoryViewModel", "Nav argument로 받은 parentId: $argParentId")
        Log.d("CategoryViewModel", "Nav argument로 받은 path: $pathString")

        // ✨ 3. 전달받은 경로 문자열을 리스트로 변환하고 맨 앞에 "전체"를 추가
        val currentPath = pathString?.split(",")
            ?.filter { it.isNotEmpty() } ?: emptyList()
        _categoryPath.value = listOf("전체") + currentPath
    }

    // 이 부분은 기존 코드와 완전히 동일합니다.
    val categories: StateFlow<List<CategoryEntity>> =
        categoryRepository.getCategoriesByParentId(parentId)
            .onEach { categoryList ->
                Log.d("CategoryViewModel", "받아온 카테고리 리스트: $categoryList")
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
}
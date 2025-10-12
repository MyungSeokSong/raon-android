//package com.example.raon.features.category.ui
//
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.raon.navigation.Category // 👈 아래에서 만들 Category 모델
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//// ViewModel이 UI에 전달할 데이터 상태
//data class CategoryUiState(
//    val categories: List<Category> = emptyList(),
//    val currentCategoryPath: String = "카테고리 선택" // TODO: 상위 카테고리 경로 표시용
//)
//
//@HiltViewModel
//class CategoryViewModel @Inject constructor(
//    // TODO: 나중에 실제 CategoryRepository를 주입받아야 합니다.
//    savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    // NavController가 전달한 parentId를 받습니다. (예: "1" -> 1L)
//    private val parentId: Long? = savedStateHandle.get<String>("parentId")?.toLongOrNull()
//
//    private val _uiState = MutableStateFlow(CategoryUiState())
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        loadCategories()
//    }
//
//    private fun loadCategories() {
//        viewModelScope.launch {
//            // --- UI 개발을 위한 임시 더미 데이터 ---
//            // TODO: 나중에 이 부분을 categoryRepository.getCategories(parentId) 호출로 교체해야 합니다.
//            val allCategories = listOf(
//                Category(1, "여성의류", false),
//                Category(2, "남성의류", false),
//                Category(3, "신발", false),
//                Category(26, "아우터", false, 1),
//                Category(27, "상의", false, 1),
//                Category(198, "패딩", true, 26),
//                Category(199, "점퍼", true, 26)
//            )
//
//            // parentId에 맞는 자식 카테고리만 필터링합니다.
//            // parentId가 null이면, parentId 필드가 없는 최상위 카테고리를 찾습니다.
//            val filteredList = if (parentId == null) {
//                allCategories.filter { it.parentId == null }
//            } else {
//                allCategories.filter { it.parentId == parentId }
//            }
//
//            _uiState.value = _uiState.value.copy(categories = filteredList)
//            // --- 임시 더미 데이터 끝 ---
//        }
//    }
//}
//

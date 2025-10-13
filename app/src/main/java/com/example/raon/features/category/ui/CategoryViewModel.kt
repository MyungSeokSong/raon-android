package com.example.raon.features.category.ui


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.features.category.data.local.CategoryEntity
import com.example.raon.features.category.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * CategoryScreen의 UI 상태와 비즈니스 로직을 관리합니다.
 * Hilt를 통해 필요한 Repository와 SavedStateHandle을 주입받습니다.
 *
 * @param categoryRepository 데이터 소스(DB)와 통신하는 저장소.
 * @param savedStateHandle Navigation Component를 통해 전달된 인자(argument)에 접근하고,
 * 프로세스 종료 후에도 상태를 저장/복원하는 데 사용됩니다.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle // Navigation Argument를 받기 위해 주입
) : ViewModel() {

    // 1. SavedStateHandle에서 "parentId" 키로 전달된 값을 가져옵니다.
    //    NavGraph에서 설정한 defaultValue(-1) 때문에, 값이 없으면 -1이 됩니다.
    private val argParentId: Int = savedStateHandle.get<Int>("parentId") ?: -1

    // 2. DAO 쿼리에서 최상위 레벨을 조회하려면 null을 사용해야 하므로, -1을 null로 변환합니다.
    private val parentId: Int? = if (argParentId == -1) null else argParentId

    // 3. UI(Screen)가 관찰할 카테고리 목록 상태입니다.
    //    parentId 값에 따라 적절한 하위 카테고리 목록을 DB에서 가져옵니다.
    val categories: StateFlow<List<CategoryEntity>> =
        categoryRepository.getCategoriesByParentId(parentId)
            .stateIn(
                scope = viewModelScope, // ViewModel의 생명주기와 함께 Flow를 관리합니다.
                // 5초 동안 구독자가 없으면 Flow 공유를 중지하여 리소스를 절약합니다.
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList() // 데이터 로딩 전 초기값은 빈 리스트입니다.
            )
}
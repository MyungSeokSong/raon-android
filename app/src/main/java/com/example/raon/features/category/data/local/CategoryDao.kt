package com.example.raon.features.category.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // 여러 카테고리를 한 번에 삽입합니다. 충돌 시에는 데이터를 덮어씁니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    // 테이블에 데이터가 하나라도 있는지 확인하기 위해 카운트를 셉니다.
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int


    // ✨ 이 함수로 변경 또는 추가합니다.
    // parentId가 null이면 level 1 카테고리를 가져옵니다.
    @Query("SELECT * FROM categories WHERE parentId IS :parentId")
    fun getCategoriesByParentId(parentId: Int?): Flow<List<CategoryEntity>>
}
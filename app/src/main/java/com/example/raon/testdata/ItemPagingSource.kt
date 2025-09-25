//package com.example.fooddelivery.data
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//
//class ItemPagingSource:PagingSource<Int, String>() {
//
//    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
//
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
//
//        return try {
//
//            // Elvis 연산자 사용 -> params.key가 Null이면 1로 초기화
//            val pageNumber = params.key ?: 1
//
////            val response =
//
//            val listData = listOf("test1", "test2", "test3")
//
//            RetrofitInstance.api.getItems()
//
//            LoadResult.Page(
//                data = listData,
//                prevKey = if( pageNumber == 1) null else pageNumber - 1,
//                nextKey = if(false) null else pageNumber + 1
//            )
//
//        } catch (e: Exception){
//            LoadResult.Error(e)
//        }
//    }
//
//}
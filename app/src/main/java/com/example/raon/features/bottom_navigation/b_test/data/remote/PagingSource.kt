package com.example.raon.features.bottom_navigation.b_test.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState

class PagingSource : PagingSource<Int, String>() {
    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        TODO("Not yet implemented")
    }
}
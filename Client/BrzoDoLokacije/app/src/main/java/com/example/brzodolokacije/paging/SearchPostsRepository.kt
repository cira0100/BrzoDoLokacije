package com.example.brzodolokacije.paging

import android.app.Activity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.SearchParams
import com.example.brzodolokacije.Services.RetrofitHelper
import kotlinx.coroutines.flow.Flow

class SearchPostsRepository(val activity: Activity,val searchParams: SearchParams) {
    companion object{
        const val DEFAULT_PAGE_SIZE=20
        const val DEFAULT_PAGE_INDEX=1
        const val PREFETCH_DISTANCE=6

        fun getInstance(activity: Activity,searchParams: SearchParams)=SearchPostsRepository(activity,searchParams)
    }

    fun letSearchedPostsFlow(pagingConfig: PagingConfig=getDefaultPageConfig()): Flow<PagingData<PostPreview>> {
        return Pager(
            config=pagingConfig,
            pagingSourceFactory = {SearchPostsPagingSource(RetrofitHelper.getInstance(),activity,searchParams)}
        ).flow
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize= DEFAULT_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE, enablePlaceholders = false)
    }
}
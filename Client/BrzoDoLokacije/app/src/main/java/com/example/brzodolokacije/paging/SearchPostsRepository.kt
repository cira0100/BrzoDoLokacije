package com.example.brzodolokacije.paging

import android.app.Activity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import kotlinx.coroutines.flow.Flow

class SearchPostsRepository(val activity: Activity) {
    companion object{
        const val DEFAULT_PAGE_SIZE=20
        const val DEFAULT_PAGE_INDEX=1

        fun getInstance(activity: Activity)=SearchPostsRepository(activity)
    }

    fun letSearchedPostsFlow(pagingConfig: PagingConfig=getDefaultPageConfig()): Flow<PagingData<PostPreview>> {
        return Pager(
            config=pagingConfig,
            pagingSourceFactory = {SearchPostsPagingSource(RetrofitHelper.getInstance(),activity)}
        ).flow
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize= DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    }
}
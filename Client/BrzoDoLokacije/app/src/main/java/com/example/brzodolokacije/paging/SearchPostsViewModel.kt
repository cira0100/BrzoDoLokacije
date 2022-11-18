package com.example.brzodolokacije.paging

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import kotlinx.coroutines.flow.Flow

class SearchPostsViewModel(
    private val api:IBackendApi,
    val activity:Activity
): ViewModel() {
    val posts =
        Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = {
            SearchPostsPagingSource(api,activity)
        }).flow.cachedIn(viewModelScope)
    fun fetchPosts(): Flow<PagingData<PostPreview>>{
        return SearchPostsRepository.getInstance(activity).letSearchedPostsFlow().cachedIn(viewModelScope)
    }
}
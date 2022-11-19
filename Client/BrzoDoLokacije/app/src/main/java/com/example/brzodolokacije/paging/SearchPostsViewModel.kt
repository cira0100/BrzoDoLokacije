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
import com.example.brzodolokacije.Models.SearchParams
import kotlinx.coroutines.flow.Flow

class SearchPostsViewModel(
    private val api:IBackendApi,
    val activity:Activity
): ViewModel() {
    fun fetchPosts(searchParams: SearchParams): Flow<PagingData<PostPreview>>{
        return SearchPostsRepository.getInstance(activity,searchParams).letSearchedPostsFlow().cachedIn(viewModelScope)
    }
}
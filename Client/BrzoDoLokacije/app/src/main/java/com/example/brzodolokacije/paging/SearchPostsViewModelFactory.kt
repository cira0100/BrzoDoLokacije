package com.example.brzodolokacije.paging

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brzodolokacije.Interfaces.IBackendApi

class SearchPostsViewModelFactory(
    val api:IBackendApi,
    val activity: Activity
):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchPostsViewModel(api,activity) as T
    }
}
package com.example.brzodolokacije.paging

import android.app.Activity
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.SearchParams
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.HttpException
import java.io.IOException

class SearchPostsPagingSource(
    val backend: IBackendApi,
    val activity: Activity,
    val searchParams:SearchParams
):PagingSource<Int,PostPreview>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostPreview> {
        val page=params.key?:0
        val token=SharedPreferencesHelper.getValue("jwt", activity)
        return try{
            val response=backend.getPagedPosts("Bearer "+token,searchParams.locationId,
                page,searchParams.sorttype,searchParams.filterdate
            )
            Log.d("main","stranicenje: "+page.toString())
            LoadResult.Page(
                response.posts,prevKey=if(page==0) null else page-1,
                nextKey=if(page==response.totalpages) null else page+1
            )
        }catch(exception:IOException){
            return LoadResult.Error(exception)
        }catch(exception:HttpException){
            return LoadResult.Error(exception)
        }
    }

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, PostPreview>): Int? {
        return state.anchorPosition?.let{ anchorPosition->
            val anchorPage=state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }

}
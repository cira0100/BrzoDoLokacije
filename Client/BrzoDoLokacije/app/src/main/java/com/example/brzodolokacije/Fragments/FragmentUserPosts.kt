package com.example.brzodolokacije.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FragmentUserPosts : Fragment() {
    private lateinit var posts : MutableList<PostPreview>
    private lateinit var rvPosts: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_user_posts, container, false)
        // Inflate the layout for this fragment
        rvPosts=view.findViewById(R.id.rvFragmentUserPostsPosts) as RecyclerView
        getPosts()
        return view
    }

    fun getPosts(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getMyPosts("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    return
                }
                posts = response.body()!!.toMutableList<PostPreview>()
                loadPosts()
            }
            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {

            }
        })
    }
    private fun loadPosts(){//most viewed
        rvPosts.apply {
            layoutManager= GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter= ShowPostsAdapter(requireActivity(),posts)

        }
    }


}
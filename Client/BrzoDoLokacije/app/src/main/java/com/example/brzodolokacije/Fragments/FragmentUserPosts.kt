package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.R

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ActivityCapturePost
import com.example.brzodolokacije.Activities.ActivityForgottenPassword
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.MyPostsAdapter
import com.example.brzodolokacije.UserPostsMapFragment

import com.google.android.material.bottomsheet.BottomSheetDialog


class FragmentUserPosts : Fragment() {
    private lateinit var posts : MutableList<PostPreview>
    private lateinit var rvPosts: RecyclerView
    private lateinit var addNewPost:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_user_posts, container, false)

        addNewPost=view.findViewById<View>(R.id.tvFragmentUserPostsAddPost) as TextView
        addNewPost.setOnClickListener {
            val bundle = Bundle()
            var jwtString=SharedPreferencesHelper.getValue("jwt",requireActivity())
            if(jwtString!=null) {
                var jwt: JWT = JWT(jwtString!!)
                var userId=jwt.getClaim("id").asString()
                bundle.putString("id", userId)
                val userMapFragment = UserPostsMapFragment()
                userMapFragment.setArguments(bundle)
                var act=requireActivity()as NavigationActivity
                act.supportFragmentManager.beginTransaction().replace(
                    R.id.flNavigationFragment,userMapFragment
                )
                    .commit()
            }

        }

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
            adapter= MyPostsAdapter(requireActivity(),posts)

        }
    }

}
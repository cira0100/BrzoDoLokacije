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
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ActivityCapturePost
import com.example.brzodolokacije.Activities.ActivityForgottenPassword
import com.example.brzodolokacije.Adapters.MyPostsAdapter

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
            var bottomSheetDialog2: BottomSheetDialog
            bottomSheetDialog2= BottomSheetDialog(requireContext())
            bottomSheetDialog2.setContentView(R.layout.bottom_sheet_add_new_post)
            bottomSheetDialog2.show()

            var close=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostClose) as ImageButton
            var openAddPost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenAddPost) as ImageButton
            var capturePost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenCapturePost) as ImageButton

            openAddPost.setOnClickListener{
                val intent = Intent (getActivity(), ActivityAddPost::class.java)
                getActivity()?.startActivity(intent)
            }

            capturePost.setOnClickListener{
                val intent = Intent (getActivity(), ActivityCapturePost::class.java)
                getActivity()?.startActivity(intent)
            }
            close.setOnClickListener {
                bottomSheetDialog2.dismiss()
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
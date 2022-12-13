package com.example.brzodolokacije.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentShowPostsByLocation : Fragment() {

    private lateinit var posts : MutableList<PostPreview>
    private lateinit var rvPosts:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_show_posts_by_location, container, false)

        rvPosts=view.findViewById(R.id.rvFragmentShowPostsByLocationPosts)as RecyclerView

        val args=this.arguments
        val filterString=args?.get("data").toString()

        getPosts(filterString)



        return view
    }


    private fun getPosts(location:String){
        val api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(RetrofitHelper.baseUrl)
            .build()
            .create(IBackendApi::class.java)
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getPosts("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(call: Call<MutableList<PostPreview>>, response: Response<MutableList<PostPreview>>) {
                if (response.body() == null) {


                    return
                }
                //refresh list
//                Toast.makeText(
//                    activity, "get all ", Toast.LENGTH_LONG
//                ).show();
                var posts = response.body()!!.toMutableList<PostPreview>()
                showPosts(posts)
            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
                Toast.makeText(
                    activity,"nema objava", Toast.LENGTH_LONG
                ).show();
            }
        })
    }

    private fun showPosts(posts:MutableList<PostPreview>){
//        Toast.makeText(
//            activity, "get all sp ", Toast.LENGTH_LONG
//        ).show();

        rvPosts.apply {
            layoutManager= GridLayoutManager(activity,2)
            adapter= ShowPostsHomePageAdapter(posts,requireActivity())

        }

    }
}
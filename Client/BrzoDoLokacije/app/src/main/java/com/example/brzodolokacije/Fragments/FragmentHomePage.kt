package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper.baseUrl
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_single_post.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentHomePage : Fragment() {
    private lateinit var posts : MutableList<PostPreview>
    private lateinit var mostViewedPosts : MutableList<PostPreview>
    private lateinit var newestPosts : MutableList<PostPreview>
    private lateinit var bestRatedPosts:MutableList<PostPreview>
    private lateinit var rvPopular: RecyclerView
    private lateinit var rvNewest:RecyclerView
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

     }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_home_page, container, false)

        rvPopular=view.findViewById(R.id.rvFragmentHomePagePopular)
        rvNewest=view.findViewById(R.id.rvFragmentHomePageLatest)
        //pokupi sve objave iz baze'
            getAllPosts()





        return view
    }
    private fun getAllPosts(){
        val api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(IBackendApi::class.java)
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getPosts("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>>{
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    Toast.makeText(
                        activity, "get all null", Toast.LENGTH_LONG
                    ).show();

                    return
                }
                //refresh list
                Toast.makeText(
                    activity, "get all ", Toast.LENGTH_LONG
                ).show();
                posts = response.body()!!.toMutableList<PostPreview>()
                getPopularPosts(posts)
                getNewestPosts(posts)
                getBestRatedPosts(posts)
            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
                Toast.makeText(
                    activity,"nema objava", Toast.LENGTH_LONG
                ).show();
            }
        })
    }

    private fun getPopularPosts(allPosts:MutableList<PostPreview>){//most viewed
        Toast.makeText(
            activity, "get all mv ", Toast.LENGTH_LONG
        ).show();
        mostViewedPosts=allPosts
        mostViewedPosts.sortByDescending { it.views }
        rvPopular.apply {
            layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            adapter=ShowPostsHomePageAdapter(mostViewedPosts)

        }

    }
    private fun getNewestPosts(allPosts:MutableList<PostPreview>){
        Toast.makeText(
            activity, "get all r ", Toast.LENGTH_LONG
        ).show();
        newestPosts=allPosts/// izmeniti nakon dodavanja datuma u model!!!!!!
        newestPosts.sortBy { it.ratings}
        rvNewest.apply {
            layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            adapter=ShowPostsHomePageAdapter(newestPosts)
        }
    }

    private fun getBestRatedPosts(allPosts:MutableList<PostPreview>){
        Toast.makeText(
            activity, "get all br ", Toast.LENGTH_LONG
        ).show();
        bestRatedPosts=allPosts
        bestRatedPosts.sortByDescending { it.ratings }
    }



}
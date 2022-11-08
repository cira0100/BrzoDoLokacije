package com.example.brzodolokacije.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.SampleAdapter
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.FragmentHomeBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class FragmentShowPosts : Fragment() {

    private lateinit var binding: FragmentShowPosts
    private var posts : MutableList<PostPreview> = mutableListOf()
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //load data for the list
        loadData()
        Log.d("main","greska")
        //instantiate adapter and linearLayout
        val postApi= RetrofitHelper.getInstance()
        val token=SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request=postApi.getPosts("Bearer "+token)

        request.enqueue(object : retrofit2.Callback<MutableList<PostPreview>?> {
            override fun onResponse(call: Call<MutableList<PostPreview>?>, response: Response<MutableList<PostPreview>?>) {
                if(response.isSuccessful){
                    posts=response.body()!!
                    recyclerView?.adapter=ShowPostsAdapter(requireActivity(),posts)
                    Toast.makeText(
                        activity, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()
                }else{
                    if(response.errorBody()!=null)
                        Toast.makeText(activity, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                }


            }

            override fun onFailure(call: Call<MutableList<PostPreview>?>, t: Throwable) {
                Toast.makeText(
                    activity, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })

        adapterVar=ShowPostsAdapter(requireActivity(),posts)
        layoutManagerVar= LinearLayoutManager(activity)
    }

    private fun loadData() {
        posts.add(PostPreview("123","asdasd",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid",13,
            4.3f,mutableListOf(),mutableListOf()))
        posts.add(PostPreview("123","asdasd",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid",13,
            4.3f,mutableListOf(),mutableListOf()))
        posts.add(PostPreview("123","asdasd",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid",13,
            4.3f,mutableListOf(),mutableListOf()))
        posts.add(PostPreview("123","asdasd",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid",13,
            4.3f,mutableListOf(),mutableListOf()))
           }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_show_posts, container, false)
        recyclerView = rootView?.findViewById(R.id.rvMain)
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        return rootView
    }

}
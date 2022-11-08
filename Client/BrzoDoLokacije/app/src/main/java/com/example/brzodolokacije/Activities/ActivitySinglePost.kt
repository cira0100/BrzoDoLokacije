package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding


class ActivitySinglePost : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null
    private lateinit var post:PostPreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        post= intent.extras?.getParcelable("selectedPost")!!
        //load data for the list

        //instantiate adapter and linearLayout
        adapterVar= PostImageAdapter(this@ActivitySinglePost, post.images as MutableList<PostImage>)
        layoutManagerVar= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerView = binding.rvMain
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        loadTextComponents()
    }

    private fun loadImages(){

    }

    private fun loadTextComponents() {
        binding.apply {
            tvTitle.text= post.location.name
            tvTitle.invalidate()
            tvLocationType.text="TODO"
            tvLocationType.invalidate()
            tvLocationParent.text="TODO"
            tvLocationParent.invalidate()
            tvRating.text=post.ratings.toString()
            tvRating.invalidate()
            tvNumberOfRatings.text=post.ratings.toString()
            tvNumberOfRatings.invalidate()
            tvDescription.text=post.description
            tvDescription.invalidate()
        }
    }

}
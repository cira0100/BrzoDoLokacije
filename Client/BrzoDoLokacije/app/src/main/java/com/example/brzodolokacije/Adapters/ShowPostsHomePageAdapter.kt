package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import java.security.AccessController.getContext

class ShowPostsHomePageAdapter(var postPreview:MutableList<PostPreview>,val activity:Activity):
    RecyclerView.Adapter<ShowPostsHomePageAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        Log.d("main","***********************************************adapter******************************************************")
        val view=LayoutInflater.from(parent.context).inflate(R.layout.post_item_home_page,parent,false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        return holder.bindView(postPreview[position] )
    }

    override fun getItemCount(): Int {
        return postPreview.size
    }


inner class PostViewHolder(view: View):RecyclerView.ViewHolder(view){
    private val background:com.google.android.material.imageview.ShapeableImageView=view.findViewById(R.id.ivPIHPBackground)
    private val locationName:TextView=view.findViewById(R.id.tvPIHPLocationName)

    fun bindView(postPreview:PostPreview){
        //background.setImageURI(postPreview.images[0]._id.to)
        if(postPreview.images.isNotEmpty()) {
            Glide.with(activity)
                .load(RetrofitHelper.baseUrl + "/api/post/image/" + postPreview.images[0]._id)
                .into(background)
        }
        locationName.text=postPreview.location.name
    }
}
    }
package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper

class ShowPostsGridViewAdapter(var postPreview:MutableList<PostPreview>, val activity: Activity):
    RecyclerView.Adapter<ShowPostsGridViewAdapter.PostViewHolder1>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder1 {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.post_item_grid_view,parent,false)
        return PostViewHolder1(view)
    }
    override fun onBindViewHolder(holder: ShowPostsGridViewAdapter.PostViewHolder1, position: Int) {
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(activity, ActivitySinglePost::class.java)
            var b= Bundle()
            postPreview[position].location.type= LocationType.ADA
            b.putParcelable("selectedPost", postPreview[position])
            intent.putExtras(b)
            activity.startActivity(intent)
        }
        return holder.bindView(postPreview[position] )
    }
    override fun getItemCount(): Int {
        return postPreview.size
    }
    inner class PostViewHolder1(view: View): RecyclerView.ViewHolder(view){
        private val background:com.google.android.material.imageview.ShapeableImageView=view.findViewById(
            R.id.postItemGridViewImage)
        private val multipleImageIcon: ImageView =view.findViewById(R.id.ivPostItemMultipleImagesIcon)

        fun bindView(postPreview: PostPreview){
            if(postPreview.images.isNotEmpty()) {
                Glide.with(activity)
                    .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + postPreview.images[0]._id)
                    .into(background)
            }
            if(postPreview.images.size>1)
                multipleImageIcon.visibility= View.VISIBLE

        }
    }


}
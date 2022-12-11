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
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Models.LocationType
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
        holder.itemView.setOnClickListener {
            //Toast.makeText(activity,item._id,Toast.LENGTH_LONG).show()
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


inner class PostViewHolder(view: View):RecyclerView.ViewHolder(view){
    private val background:com.google.android.material.imageview.ShapeableImageView=view.findViewById(R.id.ivPIHPBackground)
    private val locationName:TextView=view.findViewById(R.id.tvPIHPLocationName)
    private val locationDetail:TextView=view.findViewById(R.id.tvPIHPLocationDetail)
    private val rating:TextView=view.findViewById(R.id.tvPIHPRecension)
    private val multipleImageIcon:ImageView=view.findViewById(R.id.ivMultipleImagesIcon)

    fun bindView(postPreview:PostPreview){
        //background.setImageURI(postPreview.images[0]._id.to)
        if(postPreview.images.isNotEmpty()) {
            Glide.with(activity)
                .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + postPreview.images[0]._id)
                .into(background)
        }
        locationName.text=postPreview.location.name
        rating.text=postPreview.ratings.toString()
        if(postPreview.images.size>1)
            multipleImageIcon.visibility=View.VISIBLE
        if(postPreview.location.city!=null)
            locationDetail.text=postPreview.location.city
        else
            locationDetail.text=postPreview.location.country

    }
}
    }
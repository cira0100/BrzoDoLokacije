package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivityOpenedImages
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.databinding.PostImageBinding

class PostImageAdapter(val activity: Activity, val items : MutableList<PostImage>)
    : RecyclerView.Adapter<PostImageAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: PostImageBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding= PostImageBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : PostImageBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : PostImage){
            binding.apply {
                if(item!=null) {
                    Glide.with(activity)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + item._id)
                        .into(locationImage)
                }
            }
            itemView.setOnClickListener {
                val intent: Intent = Intent(activity, ActivityOpenedImages::class.java)
                intent.putExtra("post",(activity as ActivitySinglePost).post)
                activity.startActivity(intent)
            }
        }
    }
}
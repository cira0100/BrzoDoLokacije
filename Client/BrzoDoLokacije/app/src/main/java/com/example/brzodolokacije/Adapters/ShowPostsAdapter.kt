package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.PostPreviewBinding


class ShowPostsAdapter (val activity:Activity,val items : MutableList<PostPreview>?=null)
    : PagingDataAdapter<PostPreview, ShowPostsAdapter.ViewHolder>(REPO_COMPARATOR) {
    private lateinit var token: String
    private lateinit var imageApi: IBackendApi

    companion object{
        private val REPO_COMPARATOR=object:DiffUtil.ItemCallback<PostPreview>(){
            override fun areContentsTheSame(oldItem: PostPreview, newItem: PostPreview): Boolean {
                return oldItem._id==newItem._id
            }

            override fun areItemsTheSame(oldItem: PostPreview, newItem: PostPreview): Boolean {
                return oldItem._id==newItem._id
            }
        }
    }


    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: PostPreviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        imageApi= RetrofitHelper.getInstance()
        token= SharedPreferencesHelper.getValue("jwt", activity).toString()
        binding = PostPreviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //sets components of particular item
        holder.bind(getItem(position)!!)
        holder.itemView.setOnClickListener {

            val intent:Intent = Intent(activity,ActivitySinglePost::class.java)
            var b=Bundle()
            //getItem(position)!!.location.type=LocationType.ADA
            //---------------------------------------------------------------  call back to add view tick
            //---------------------------------------------------------------
            b.putParcelable("selectedPost",getItem(position)!!)
            intent.putExtras(b)
            activity.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: PostPreviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: PostPreview) {
            binding.apply {
                tvTitle.text = item.location.name
                tvLocationParent.text = item.location.country
                //tvLocationType.text = "TODO"
                if(item.images.isNotEmpty()) {
                    Glide.with(activity)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + item.images[0]._id)
                        .into(locationImage)
                }

            }
        }
    }
}
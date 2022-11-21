package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.PostPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class MyPostsAdapter (val activity:Activity,val items : MutableList<PostPreview>)
    : RecyclerView.Adapter<MyPostsAdapter.ViewHolder>() {
    private lateinit var token: String
    private lateinit var imageApi: IBackendApi

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




    override fun getItemCount() = items.size
    inner class ViewHolder(itemView: PostPreviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: PostPreview) {
            binding.apply {
                tvTitle.text = item.location.name
                tvLocationParent.text = item.location.country
                tvLocationType.text = "TODO"
                if(item.images.isNotEmpty()) {
                    Glide.with(activity)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/" + item.images[0]._id)
                        .into(locationImage)
                }

            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            //Toast.makeText(activity,item._id,Toast.LENGTH_LONG).show()
            val intent:Intent = Intent(activity,ActivitySinglePost::class.java)
            var b=Bundle()
            items[position].location.type=LocationType.ADA
            b.putParcelable("selectedPost", items[position])
            intent.putExtras(b)
            activity.startActivity(intent)
        }
    }
}

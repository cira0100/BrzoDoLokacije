package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.databinding.OpenedPostImageBinding

class OpenedPostImageAdapter(val items:List<PostImage>?,val activity:Activity): RecyclerView.Adapter<OpenedPostImageAdapter.ViewHolder>() {
    lateinit var binding:OpenedPostImageBinding

    inner class ViewHolder(itemView: OpenedPostImageBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item:PostImage){
            binding.apply {
                if(item!=null) {
                    Glide.with(activity)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + item._id)
                        .into(ivOpenedImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding= OpenedPostImageBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int {
        if(items==null){
            return 0
        }
        else{
            return items.size
        }
    }

}
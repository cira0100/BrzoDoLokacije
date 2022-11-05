package com.example.brzodolokacije.Adapters

import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.databinding.PostImageBinding
import com.example.brzodolokacije.databinding.PostPreviewBinding

class PostImageAdapter(val items : MutableList<java.io.File>)
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
        fun bind(item : java.io.File){
            binding.apply {
                locationImage.setImageBitmap(BitmapFactory.decodeStream(item.inputStream()))
            }
        }
    }
}
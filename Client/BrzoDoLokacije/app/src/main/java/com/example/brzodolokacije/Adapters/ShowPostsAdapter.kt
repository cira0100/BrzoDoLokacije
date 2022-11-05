package com.example.brzodolokacije.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Models.ListItemModel
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.databinding.ListItemBinding
import com.example.brzodolokacije.databinding.PostPreviewBinding
import java.io.Console

class ShowPostsAdapter (val items : MutableList<PostPreview>)
    : RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: PostPreviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=PostPreviewBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
/*        holder.itemView.setOnClickListener {
            fun listener(position: Int){
                binding.tvTitle.text="klik"
            }
        }*/
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : PostPreviewBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : PostPreview){
            binding.apply {
                tvTitle.text=item.location.name
                tvLocationParent.text=item.location.country
                tvLocationType.text=item.location.type.toString()
            }
        }
    }
}
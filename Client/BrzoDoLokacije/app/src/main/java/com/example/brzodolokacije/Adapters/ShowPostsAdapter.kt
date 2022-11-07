package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.databinding.PostPreviewBinding

class ShowPostsAdapter (val activity:Activity,val items : MutableList<PostPreview>)
    : RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>() {
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: PostPreviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = PostPreviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //sets components of particular item
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            //Toast.makeText(activity,item._id,Toast.LENGTH_LONG).show()
            val intent:Intent = Intent(activity,ActivitySinglePost::class.java)
            var b=Bundle()
            b.putParcelable("selectedPost", items[position])
            intent.putExtras(b)
            activity.startActivity(intent)
        }
    }


    override fun getItemCount() = items.size
    inner class ViewHolder(itemView: PostPreviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: PostPreview) {
            binding.apply {
                tvTitle.text = item.location.name
                tvLocationParent.text = item.location.country
                tvLocationType.text = item.location.type.toString()

                itemView.isClickable = true

            }
        }
    }
}
package com.example.brzodolokacije.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.databinding.SingleCommentBinding

class CommentsAdapter (val items : MutableList<CommentSend>)
    : RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: SingleCommentBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=SingleCommentBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        try{
            return items.size
        }catch (e:Exception){
            return 0
        }
    }
    inner class ViewHolder(itemView : SingleCommentBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : CommentSend){
            binding.apply {
                tvCommentAuthor.text=item.username
                tvCommentText.text=item.comment
            }
        }
    }
}
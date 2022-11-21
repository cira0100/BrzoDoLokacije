package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ChatActivityConversation
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.databinding.ChatPreviewBinding

class ChatPreviewsAdapter (val items : MutableList<ChatPreview>,val activity:Activity)
    : RecyclerView.Adapter<ChatPreviewsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: ChatPreviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=ChatPreviewBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(activity, ChatActivityConversation::class.java)
            intent.putExtra("userId",items[position].userId)
            activity.startActivity(intent)
        }
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : ChatPreviewBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : ChatPreview){
            binding.apply {
                tvUsername.text=item.userId
            }
        }
    }
}
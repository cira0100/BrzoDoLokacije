package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ChatMessageBinding
import com.example.brzodolokacije.databinding.ChatMessageOtherBinding

class ChatMessagesAdapter (val items : MutableList<Message>, val activity:Activity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private val VIEW_TYPE_MESSAGE_SENT: Int = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED:Int = 2
    private var binding: ChatMessageBinding?=null
    private var bindingOther: ChatMessageOtherBinding?=null

    private var currentUser:String?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var token=SharedPreferencesHelper.getValue("jwt",activity)
        var jwt= JWT(token.toString())
        currentUser= jwt.getClaim("id").toString()

        if(viewType==VIEW_TYPE_MESSAGE_RECEIVED){
            bindingOther= ChatMessageOtherBinding.inflate(inflater,parent,false)
            return ReceivedViewHolder(bindingOther!!)
        }
        else{
            binding= ChatMessageBinding.inflate(inflater,parent,false)
            return SentViewHolder(binding!!)
        }

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        //sets components of particular item
        if(holder is ReceivedViewHolder){
            holder.bind(items[position])
        }
        else if(holder is SentViewHolder){
            holder.bind(items[position])
        }
    }
    override fun getItemCount() = items.size
    inner class ReceivedViewHolder(itemView : ChatMessageOtherBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : Message){
            bindingOther?.apply {
                tvMessage?.text=item.messagge
            }
        }
    }
    inner class SentViewHolder(itemView : ChatMessageBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : Message){
            binding?.apply {
                tvMessage.text=item.messagge
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        var sender=items.get(position).senderId
        if(sender==currentUser){
            return VIEW_TYPE_MESSAGE_SENT
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }
}
package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ChatMessageBinding
import com.example.brzodolokacije.databinding.ChatMessageOtherBinding
import java.util.*

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
                var hour=item.usableTimeStamp.get(Calendar.HOUR_OF_DAY)
                var hourStr=if(hour<10) "0"+hour.toString() else hour.toString()
                var minute=item.usableTimeStamp.get(Calendar.MINUTE)
                var minuteStr=if(minute<10) "0"+minute.toString() else minute.toString()
                tvTimestamp.text= hourStr + ":" + minuteStr
                if(layoutPosition==0 || isDifferentDays(items[layoutPosition].usableTimeStamp,items[layoutPosition-1].usableTimeStamp)){

                    tvDate.text=item.usableTimeStamp.get(Calendar.DAY_OF_MONTH).toString()+"/"+
                            (item.usableTimeStamp.get(Calendar.MONTH)+1).toString()+"/"+
                            item.usableTimeStamp.get(Calendar.YEAR).toString()
                }
                else{
                    tvDate.visibility= View.GONE
                    tvDate.forceLayout()
                }
            }
        }
    }
    inner class SentViewHolder(itemView : ChatMessageBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : Message){
            binding?.apply {
                tvMessage.text=item.messagge
                var hour=item.usableTimeStamp.get(Calendar.HOUR_OF_DAY)
                var hourStr=if(hour<10) "0"+hour.toString() else hour.toString()
                var minute=item.usableTimeStamp.get(Calendar.MINUTE)
                var minuteStr=if(minute<10) "0"+minute.toString() else minute.toString()
                tvTimestamp.text= hourStr + ":" + minuteStr
                if(layoutPosition==0 || isDifferentDays(items[layoutPosition].usableTimeStamp,items[layoutPosition-1].usableTimeStamp)){

                    tvDate.text=item.usableTimeStamp.get(Calendar.DAY_OF_MONTH).toString()+"/"+
                            (item.usableTimeStamp.get(Calendar.MONTH)+1).toString()+"/"+
                            item.usableTimeStamp.get(Calendar.YEAR).toString()
                }
                else{
                    tvDate.visibility= View.GONE
                    tvDate.forceLayout()
                }
            }
        }
    }

    fun isDifferentDays(c1:Calendar,c2:Calendar):Boolean{
        if(c1.get(Calendar.DAY_OF_YEAR)!=c2.get(Calendar.DAY_OF_YEAR)){
            return true
        }
        else if(c1.get(Calendar.YEAR)!=c2.get(Calendar.YEAR)){
            return true
        }
        return false
    }


    override fun getItemViewType(position: Int): Int {
        var sender=items.get(position).senderId
        var token= SharedPreferencesHelper.getValue("jwt",activity)
        var jwt= JWT(token.toString())
        var claim=jwt.getClaim("id")
        currentUser= claim.asString()!!
        if(sender==currentUser){
            return VIEW_TYPE_MESSAGE_SENT
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }
}
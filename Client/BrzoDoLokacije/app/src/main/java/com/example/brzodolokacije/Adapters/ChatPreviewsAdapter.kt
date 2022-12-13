package com.example.brzodolokacije.Adapters

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.exam.DBHelper
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Activities.ChatActivityConversation
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ChatPreviewBinding
import kotlinx.android.synthetic.main.chat_preview.view.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class ChatPreviewsAdapter (val items : MutableList<ChatPreview>,val activity:ChatActivity)
    : RecyclerView.Adapter<ChatPreviewsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private var api: IBackendApi?=null
    private var token:String?=null
    private lateinit var binding: ChatPreviewBinding
    private var db:DBHelper=DBHelper.getInstance(activity)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=ChatPreviewBinding.inflate(inflater,parent,false)
        api=RetrofitHelper.getInstance()
        token=SharedPreferencesHelper.getValue("jwt",activity)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : ChatPreviewBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : ChatPreview){
            binding.apply {
                val request2=api?.getProfileFromId("Bearer "+token,
                    item.userId
                )
                request2?.enqueue(object : retrofit2.Callback<UserReceive?> {
                    override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                        if(response.isSuccessful()){
                            //zahtev da se posalje poruka
                            var user=response.body()!!
                            if(!item.read)
                                setUnread()
                            tvUsername.text=user.username
                            if(user.pfp!=null) {
                                Glide.with(activity)
                                    .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + user.pfp!!._id)
                                    .circleCrop()
                                    .into(ivUserImage)
                            }
                        }
                        else{
                            Toast.makeText(activity,"los id",
                                Toast.LENGTH_LONG).show()
                            tvUsername.text="nije nadjen korisnik"
                        }
                    }

                    override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                        Toast.makeText(activity,"neuspesan zahtev",
                            Toast.LENGTH_LONG).show()
                    }
                })
                var lastMessage=db.getLastMessage(item.userId)
                tvUsername.text=item.username
                if(lastMessage!=null){
                    //var msg=lastMessage.messagge.dropLast(if(lastMessage.messagge.length>20) lastMessage.messagge.length-20 else 0)
                    tvLastMessage.text=lastMessage.messagge//msg+if(lastMessage.messagge.length>20) "..." else ""
                    if(!isDifferentDays(lastMessage.usableTimeStamp,Calendar.getInstance())){
                        var hour=lastMessage.usableTimeStamp.get(Calendar.HOUR_OF_DAY)
                        var hourStr=if(hour<10) "0"+hour.toString() else hour.toString()
                        var minute=lastMessage.usableTimeStamp.get(Calendar.MINUTE)
                        var minuteStr=if(minute<10) "0"+minute.toString() else minute.toString()
                        tvLastMessageDate.text= hourStr + ":" + minuteStr
                    }
                    else{
                        tvLastMessageDate.text=lastMessage.usableTimeStamp.get(Calendar.DAY_OF_MONTH).toString()+"/"+
                                (lastMessage.usableTimeStamp.get(Calendar.MONTH)+1).toString()+"/"+
                                lastMessage.usableTimeStamp.get(Calendar.YEAR).toString()
                    }

                }
                itemView.setOnClickListener {
                    val intent: Intent = Intent(activity, ChatActivityConversation::class.java)
                    intent.putExtra("userId",items[position].userId)
                    intent.putExtra("username",itemView.tvUsername.text)
                    intent.putExtra("pfp",itemView.ivUserImage.drawable.toBitmap(200,200))
                    db.readContact(items[position].userId)
                    items[position].read=true
                    setRead()
                    activity.startActivity(intent)
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
        fun setUnread(){
            itemView.tvUsername.typeface= Typeface.DEFAULT_BOLD
            itemView.tvUsername.invalidate()
            itemView.tvLastMessage.typeface= Typeface.DEFAULT_BOLD
            itemView.tvLastMessage.invalidate()
            itemView.tvLastMessageDate.typeface= Typeface.DEFAULT_BOLD
            itemView.tvLastMessageDate.invalidate()
            itemView.readIndicator.background= ContextCompat.getDrawable(activity,R.color.dark_blue_transparent)
            itemView.readIndicator.invalidate()
        }

        fun setRead(){
            itemView.tvUsername.typeface= Typeface.DEFAULT
            itemView.tvUsername.invalidate()
            itemView.tvLastMessage.typeface= Typeface.DEFAULT
            itemView.tvLastMessage.invalidate()
            itemView.tvLastMessageDate.typeface= Typeface.DEFAULT
            itemView.tvLastMessageDate.invalidate()
            itemView.readIndicator.background= ContextCompat.getDrawable(activity,R.color.white)
            itemView.readIndicator.invalidate()
        }
    }
}
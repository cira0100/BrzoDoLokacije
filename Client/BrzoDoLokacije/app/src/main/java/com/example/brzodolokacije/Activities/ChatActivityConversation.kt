package com.example.brzodolokacije.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.exam.DBHelper
import com.example.brzodolokacije.Adapters.ChatMessagesAdapter
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Models.MessageSend
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatConversationBinding
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*

class ChatActivityConversation : AppCompatActivity() {

    var recyclerView:RecyclerView?=null
    var adapterVar: ChatMessagesAdapter?=null
    var layoutVar: RecyclerView.LayoutManager?=null
    lateinit var binding:ActivityChatConversationBinding
    var userId:String?=null
    var receiverUsername:String?=null
    var dbConnection: DBHelper?=null
    var webSocketConnection:SignalRListener?=null
    var items:MutableList<Message>?=mutableListOf()
    var userImage:Bitmap?=null
    var userImageId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId=intent.extras?.get("userId").toString()
        receiverUsername=intent.extras?.get("username").toString()
        if(intent.extras?.get("pfp") is Bitmap){
            userImage=intent.extras?.get("pfp") as Bitmap?
        }
        else{
            userImageId=intent.extras?.get("pfp") as String?
        }
        dbConnection=DBHelper.getInstance(this@ChatActivityConversation)
        setHeader()
        setRecyclerView()
        requestMessages()
        webSocketConnection=SignalRListener.getInstance(this@ChatActivityConversation)
        if(webSocketConnection!!.activity is ChatActivity)
            (webSocketConnection!!.activity as ChatActivity).setClickedActivity(this@ChatActivityConversation)
        setListeners()
    }

    private fun setListeners() {
        findViewById<ImageButton>(R.id.btnSendMessage).setOnClickListener {
            var token=SharedPreferencesHelper.getValue("jwt",this@ChatActivityConversation)
            var messageContent=findViewById<EditText>(R.id.etNewMessage).text.trim().toString()
            val Api= RetrofitHelper.getInstance()
            if(!messageContent.isNullOrEmpty()){
                    //zahtev da se posalje poruka
                    var message= MessageSend(userId!!,messageContent)
                    val request2=Api.sendMessage("Bearer "+token,
                        message
                    )
                    request2.enqueue(object : retrofit2.Callback<Message?> {
                        override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                            if(response.isSuccessful()){
                                //zahtev da se posalje poruka
                                var responseMessage=response.body()
                                var cal: Calendar = Calendar.getInstance()
                                cal.time=responseMessage?.timestamp
                                responseMessage?.usableTimeStamp=cal
                                dbConnection?.addMessage(responseMessage!!,username=receiverUsername)
                                requestMessages()
                                binding.etNewMessage.text?.clear()
                            }
                            else{
                                Toast.makeText(this@ChatActivityConversation,"Pogresno korisnicko ime.",Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Message?>, t: Throwable) {
                            Toast.makeText(this@ChatActivityConversation,"Pogresno korisnicko ime.",Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
        binding.llHeader.setOnClickListener {
            val intent: Intent = Intent(this@ChatActivityConversation,ActivityUserProfile::class.java)
            var b= Bundle()
            intent.putExtra("user", Gson().toJson(
                UserReceive(userId!!,"",receiverUsername!!,"",Date(),null,0, listOf(),0,listOf(),0,listOf(),0,null)
            ))
            this.startActivity(intent)
        }

    }

    private fun setHeader(){
        if(userId.isNullOrEmpty() || userId.equals("null")){
            binding.tvFragmentTitle.text="Nije nađen korisnik"
            binding.tvFragmentTitle.invalidate()
        }
        else{
            binding.tvFragmentTitle.text=receiverUsername
            binding.tvFragmentTitle.invalidate()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        if(userImage!=null){
            binding.ivUserImage.setImageBitmap(userImage)
        }
        else if(userImageId!=null){
            Glide.with(this)
                .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + userImageId!!)
                .circleCrop()
                .into(binding.ivUserImage)
        }
    }
    fun setRecyclerView(setParams:Boolean=true){
        MainScope().launch {
            if (setParams) {
                adapterVar = items?.let { ChatMessagesAdapter(it, this@ChatActivityConversation) }
                layoutVar = LinearLayoutManager(this@ChatActivityConversation)
            }
            recyclerView = binding.rvMain
            recyclerView?.setHasFixedSize(true)
            recyclerView?.layoutManager = layoutVar
            try {
                recyclerView?.adapter = adapterVar

            } catch (e: Exception) {
                Log.d("error", e.message!!)
            }
            recyclerView?.addOnLayoutChangeListener { _, i, i2, i3, i4, i5, i6, i7, i8 -> recyclerView?.scrollToPosition(items?.size?.minus(1) ?: 0) }
            recyclerView?.scrollToPosition(items?.size?.minus(1) ?: 0)
        }
    }

    fun requestMessages(){
        if(!userId.isNullOrEmpty() && !userId.equals("null")){
            if(userId!= JWT(SharedPreferencesHelper.getValue("jwt",this@ChatActivityConversation)!!).claims["id"]?.asString())
                items=dbConnection?.getMessages(userId!!)
            else
                items=dbConnection?.getMessages(userId!!,self = true)
        }
        adapterVar= items?.let { ChatMessagesAdapter(it,this@ChatActivityConversation) }
        setRecyclerView(setParams = false)
        dbConnection?.readContact(userId!!)
    }
}
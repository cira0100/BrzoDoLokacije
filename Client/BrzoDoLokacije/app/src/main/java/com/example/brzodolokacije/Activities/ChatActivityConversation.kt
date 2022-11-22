package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import retrofit2.Call
import retrofit2.Response

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId=intent.extras?.get("userId").toString()
        receiverUsername=intent.extras?.get("username").toString()
        dbConnection=DBHelper.getInstance(this@ChatActivityConversation)
        setHeader()
        setRecyclerView()
        requestMessages()
        webSocketConnection=SignalRListener.getInstance(this@ChatActivityConversation)
        setListeners()
    }

    private fun setListeners() {
        findViewById<ImageButton>(R.id.btnSendMessage).setOnClickListener {
            var token=SharedPreferencesHelper.getValue("jwt",this@ChatActivityConversation)
            var messageContent=findViewById<EditText>(R.id.etNewMessage).text.toString()
            Log.d("main",token!!)
            val Api= RetrofitHelper.getInstance()
            if(userId.isNullOrEmpty() || userId.equals("null")){
                //zahtev sa username=om
                receiverUsername=findViewById<EditText>(R.id.etReceiverUsername).text.toString()
                val request=Api.getProfile("Bearer "+token,
                    receiverUsername!!
                )
                request.enqueue(object : retrofit2.Callback<UserReceive?> {
                    override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                        if(response.isSuccessful()){
                            //zahtev da se posalje poruka
                            userId=response.body()?._id
                            var message= MessageSend(userId!!,messageContent)
                            val request2=Api.sendMessage("Bearer "+token,
                                message
                            )
                            request2.enqueue(object : retrofit2.Callback<Message?> {
                                override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                                    if(response.isSuccessful()){
                                        //zahtev da se posalje poruka
                                        var responseMessage=response.body()
                                        dbConnection?.addMessage(responseMessage!!)



                                    }
                                    else{
                                        Toast.makeText(this@ChatActivityConversation,"Pogresno korisnicko ime1.",Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<Message?>, t: Throwable) {
                                    Toast.makeText(this@ChatActivityConversation,"Pogresno korisnicko ime2.",Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                        else{
                            Log.d("main",response.message())
                            //Toast.makeText(this@ChatActivityConversation,"Pogresno korisnicko ime3.",Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                        Toast.makeText(this@ChatActivityConversation,"fail.",Toast.LENGTH_LONG).show()
                    }
                })
            }
            else{
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
                            dbConnection?.addMessage(responseMessage!!)
                            //requestMessages()
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

    }

    private fun setHeader(){
        if(userId.isNullOrEmpty() || userId.equals("null")){
            binding.tvFragmentTitle.visibility= View.GONE
            binding.tvFragmentTitle.invalidate()
            binding.tvFragmentTitle.forceLayout()
        }
        else{
            binding.tvFragmentTitle.text=receiverUsername
            binding.tvFragmentTitle.invalidate()
            binding.cvParentUsername.visibility= View.GONE
            binding.cvParentUsername.forceLayout()
        }
    }
    fun setRecyclerView(setParams:Boolean=true){
        if(setParams){
            adapterVar= items?.let { ChatMessagesAdapter(it,this@ChatActivityConversation) }
            layoutVar= LinearLayoutManager(this@ChatActivityConversation)
        }
        recyclerView = binding.rvMain
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=layoutVar
        recyclerView?.adapter=adapterVar
    }

    fun requestMessages(){
        items=dbConnection?.getMessages(userId!!)
        adapterVar= items?.let { ChatMessagesAdapter(it,this@ChatActivityConversation) }
        setRecyclerView(setParams = false)
    }
}
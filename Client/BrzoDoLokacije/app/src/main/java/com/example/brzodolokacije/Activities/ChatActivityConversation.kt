package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Models.MessageSend
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.chat.DBHelper
import com.example.brzodolokacije.chat.SignalRListener
import retrofit2.Call
import retrofit2.Response

class ChatActivityConversation : AppCompatActivity() {

    var receiverId:String?=null
    var receiverUsername:String?="jelena"
    var dbConnection:DBHelper?=null
    var webSocketConnection:SignalRListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_conversation)
        receiverId=intent.extras?.get("receiverId").toString()
        if(receiverId.isNullOrEmpty()){
            findViewById<CardView>(R.id.cvParentMessageEdit).isVisible=true
            findViewById<CardView>(R.id.cvParentMessageEdit).invalidate()
        }
        else{
            findViewById<CardView>(R.id.cvParentUsername).isVisible=true
            findViewById<CardView>(R.id.cvParentUsername).invalidate()
        }
        dbConnection=DBHelper.getInstance(this@ChatActivityConversation)
        webSocketConnection=SignalRListener.getInstance(this@ChatActivityConversation)
        setListeners()
    }

    private fun setListeners() {
        findViewById<ImageButton>(R.id.btnSendMessage).setOnClickListener {
            var token=SharedPreferencesHelper.getValue("jwt",this@ChatActivityConversation)
            var messageContent=findViewById<EditText>(R.id.etNewMessage).text.toString()
            Log.d("main",token!!)
            val Api= RetrofitHelper.getInstance()
            if(receiverId.isNullOrEmpty()){
                //zahtev sa username=om
                receiverUsername=findViewById<EditText>(R.id.etReceiverUsername).text.toString()
                val request=Api.getProfile("Bearer "+token,
                    receiverUsername!!
                )
                request.enqueue(object : retrofit2.Callback<UserReceive?> {
                    override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                        if(response.isSuccessful()){
                            //zahtev da se posalje poruka
                            receiverId=response.body()?._id
                            var message= MessageSend(receiverId!!,messageContent)
                            val request2=Api.sendMessage("Bearer "+token,
                                message
                            )
                            request2.enqueue(object : retrofit2.Callback<Message?> {
                                override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                                    if(response.isSuccessful()){
                                        //zahtev da se posalje poruka
                                        var responseMessage=response.body()
                                        dbConnection?.addMessage(responseMessage!!)
                                        dbConnection?.getMessages()
                                        webSocketConnection?.getConnectionState()


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
                var message= MessageSend(receiverId!!,messageContent)
                val request2=Api.sendMessage("Bearer "+token,
                    message
                )
                request2.enqueue(object : retrofit2.Callback<Message?> {
                    override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                        if(response.isSuccessful()){
                            //zahtev da se posalje poruka
                            var responseMessage=response.body()
                            dbConnection?.addMessage(responseMessage!!)
                            dbConnection?.getMessages()


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
}
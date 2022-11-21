package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.R
import com.example.brzodolokacije.chat.DBHelper
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private var dbConnection:DBHelper?=null
    lateinit var binding: ActivityChatBinding
    var ws:SignalRListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding=ActivityChatBinding.inflate(layoutInflater)
        dbConnection= DBHelper(this@ChatActivity,null)
        ws=SignalRListener.getInstance(this@ChatActivity)
        setListeners()

    }
    fun setListeners(){
        findViewById<ImageButton>(R.id.addNewMessage).setOnClickListener {
            val intent: Intent = Intent(this@ChatActivity,ChatActivityConversation::class.java)
            intent.putExtra("receiverId","")
            startActivity(intent)
        }
    }
}
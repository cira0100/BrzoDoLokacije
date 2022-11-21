package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exam.DBHelper
import com.example.brzodolokacije.Adapters.ChatPreviewsAdapter
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private var dbConnection:DBHelper?=null
    lateinit var binding: ActivityChatBinding
    var ws:SignalRListener?=null
    var recyclerView:RecyclerView?=null
    var adapterVar:ChatPreviewsAdapter?=null
    var layoutVar:LinearLayoutManager?=null
    var items:MutableList<ChatPreview>?= mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbConnection= DBHelper(this@ChatActivity,null)
        ws=SignalRListener.getInstance(this@ChatActivity)
        setListeners()
        setRecyclerView()
        requestForChats()

    }
    fun setListeners(){
        findViewById<ImageButton>(R.id.addNewMessage).setOnClickListener {
            val intent: Intent = Intent(this@ChatActivity,ChatActivityConversation::class.java)
            intent.putExtra("receiverId","")
            startActivity(intent)
        }
    }

    fun requestForChats(){
        var dbHelper= DBHelper.getInstance(this@ChatActivity)
        items=dbHelper.getContacts()
        adapterVar= items?.let { ChatPreviewsAdapter(it,this@ChatActivity) }
        setRecyclerView(setParams = false)
    }

    fun setRecyclerView(setParams:Boolean=true){
        if(setParams){
            adapterVar= items?.let { ChatPreviewsAdapter(it,this@ChatActivity) }
            layoutVar=LinearLayoutManager(this@ChatActivity)
        }
        recyclerView=binding.rvMain
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=layoutVar
        recyclerView?.adapter=adapterVar
    }
}
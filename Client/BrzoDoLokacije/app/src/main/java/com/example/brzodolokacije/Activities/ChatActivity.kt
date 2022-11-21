package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.exam.DBHelper
import com.example.brzodolokacije.Adapters.ChatPreviewsAdapter
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var dbConnection:DBHelper?=null
    lateinit var binding: ActivityChatBinding
    var ws:SignalRListener?=null
    var recyclerView:RecyclerView?=null
    var adapterVar:ChatPreviewsAdapter?=null
    var layoutVar:LinearLayoutManager?=null
    var items:MutableList<ChatPreview>?= mutableListOf()
    private var swipeRefreshLayout: SwipeRefreshLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbConnection= DBHelper(this@ChatActivity,null)
        ws=SignalRListener.getInstance(this@ChatActivity)
        setListeners()
        setRecyclerView()
        requestForChats()
        swipeRefreshLayout = binding.swipeContainer
        swipeRefreshLayout?.setOnRefreshListener(this@ChatActivity)
        swipeRefreshLayout?.setColorSchemeResources(
            R.color.purple_200,
            R.color.teal_200,
            R.color.dark_blue_transparent,
            R.color.purple_700
        )
        swipeRefreshLayout?.post(kotlinx.coroutines.Runnable {
            swipeRefreshLayout?.isRefreshing=true
            requestForChats()
        })

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
        swipeRefreshLayout?.isRefreshing=false
    }

    override fun onRefresh() {
        requestForChats()
    }
}
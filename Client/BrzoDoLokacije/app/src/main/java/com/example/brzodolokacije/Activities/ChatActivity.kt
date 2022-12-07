package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auth0.android.jwt.JWT
import com.exam.DBHelper
import com.example.brzodolokacije.Adapters.ChatPreviewsAdapter
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Models.MessageReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ChatActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var dbConnection:DBHelper?=null
    lateinit var binding: ActivityChatBinding
    var ws:SignalRListener?=null
    var recyclerView:RecyclerView?=null
    var adapterVar:ChatPreviewsAdapter?=null
    var layoutVar:LinearLayoutManager?=null
    var items:MutableList<ChatPreview>?= mutableListOf()
    private var swipeRefreshLayout: SwipeRefreshLayout?=null
    var clickedChat:ChatActivityConversation?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        permissionLauncher=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        //ako je  upravo odbijena dozvola na uredjaju na kome je ona neophodna
                        binding.btnNotifications.setImageResource(R.drawable.bell_off)
                    }
                    else{
                        //ako je  upravo odbijena dozvola na uredjaju na kome nije ona neophodna
                        binding.btnNotifications.setImageResource(R.drawable.bell_on)
                    }
                }
                else{
                    //ako je  upravo odbijena dozvola na uredjaju na kome nije ona neophodna
                    binding.btnNotifications.setImageResource(R.drawable.bell_on)
                }
            }
            else{
                //ako je  upravo prihvacena dozvola na uredjaju na kome nije ona neophodna
                binding.btnNotifications.setImageResource(R.drawable.bell_on)
            }
        }
        //provera da li je dozvoljeno
        when {
            ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                binding.btnNotifications.setImageResource(R.drawable.bell_on)
            }
            else -> {
                binding.btnNotifications.setImageResource(R.drawable.bell_off)
            }
        }


        setContentView(binding.root)
        dbConnection= DBHelper(this@ChatActivity,null)
        ws=SignalRListener.getInstance(this@ChatActivity)
        setListeners()
        setRecyclerView()
        swipeRefreshLayout = binding.swipeContainer
        swipeRefreshLayout?.setOnRefreshListener(this@ChatActivity)
        swipeRefreshLayout?.setColorSchemeResources(
            R.color.purple_200,
            R.color.teal_200,
            R.color.dark_blue_transparent,
            R.color.purple_700
        )
    }

    override fun onResume() {
        super.onResume()
        clickedChat=null
        requestNewMessages()
    }

    fun launchNotificationPermissionPrompt(){
        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    fun launchInfoDialog(){
        val alertDialog: AlertDialog = AlertDialog.Builder(this@ChatActivity).create()
        alertDialog.setTitle("Obaveštenje")
        alertDialog.setMessage("Potrebno je restartovati aplikaciju da bi se sačuvale promene.")
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }


    fun setListeners(){
        findViewById<ImageButton>(R.id.addNewMessage).setOnClickListener {
            val intent: Intent = Intent(this@ChatActivity,ChatActivityConversation::class.java)
            intent.putExtra("receiverId","")
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        findViewById<ImageButton>(R.id.btnNotifications).setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        revokeSelfPermissionOnKill(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    launchInfoDialog()
                }
                else -> {
                    launchNotificationPermissionPrompt()
                }
            }
        }
    }

    fun requestForChats(){
        var dbHelper= DBHelper.getInstance(this@ChatActivity)
        items=dbHelper.getContacts()
        adapterVar= items?.let { ChatPreviewsAdapter(it,this@ChatActivity) }
        setRecyclerView(setParams = false)
    }

    fun requestNewMessages(){
        val api=RetrofitHelper.getInstance()
        val token=SharedPreferencesHelper.getValue("jwt",this@ChatActivity)
        val request2=api?.getNewMessages("Bearer "+token)
        request2?.enqueue(object : Callback<MutableList<MessageReceive>?> {
            override fun onResponse(call: Call<MutableList<MessageReceive>?>, response: Response<MutableList<MessageReceive>?>) {
                if(response.isSuccessful()){
                    var messages=response.body()
                    if(!messages.isNullOrEmpty()){
                        var dbHelper= DBHelper.getInstance(this@ChatActivity)
                        for( message in messages){
                            var cal: Calendar = Calendar.getInstance()
                            cal.time=message.timestamp
                            dbHelper.addMessage(
                                Message(message.senderId+message.timestamp,message.senderId,
                                JWT(SharedPreferencesHelper.getValue("jwt",this@ChatActivity)!!).claims["id"]?.asString()!!,message.messagge,message.timestamp,cal),false)
                        }
                    }
                    requestForChats()
                }
                else{
                    Toast.makeText(this@ChatActivity,"los id",
                        Toast.LENGTH_LONG).show()
                    requestForChats()
                }
            }

            override fun onFailure(call: Call<MutableList<MessageReceive>?>, t: Throwable) {
                Toast.makeText(this@ChatActivity,"neuspesan zahtev",
                    Toast.LENGTH_LONG).show()
                requestForChats()
            }
        })

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

    fun setClickedActivity(activity:ChatActivityConversation){
        clickedChat=activity
    }

    override fun onRefresh() {
        requestNewMessages()
    }
}
package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import com.example.brzodolokacije.chat.Notifications
import com.example.brzodolokacije.chat.SignalRListener
import com.example.brzodolokacije.databinding.ActivityChatBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
        Notifications.makeChannel(this)
        setPermissionLauncher()
        checkPermissions()
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

    fun setPermissionLauncher(){
        permissionLauncher=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                binding.btnNotifications.setImageResource(R.drawable.bell_off)
            }
            else{
                binding.btnNotifications.setImageResource(R.drawable.bell_on)
            }
        }
    }

    fun checkPermissions(){
        when {
            //treba proveriti permisije
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU-> {
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    binding.btnNotifications.setImageResource(R.drawable.bell_on)
                }
                else{
                    binding.btnNotifications.setImageResource(R.drawable.bell_off)
                }
            }
            //treba proveriti preference
            else-> {
                if(SharedPreferencesHelper.getValue("notifications",this)==null){
                    SharedPreferencesHelper.addValue("notifications","false",this@ChatActivity)
                }
                else if (SharedPreferencesHelper.getValue("notifications",this)=="true") {
                    binding.btnNotifications.setImageResource(R.drawable.bell_on)
                }
                else{
                    binding.btnNotifications.setImageResource(R.drawable.bell_off)
                }
            }

        }
    }

    fun launchNotificationPermissionPrompt(){
        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    fun launchInfoDialog(){
        var builder= MaterialAlertDialogBuilder(this)
        builder.background = AppCompatResources.getDrawable(this,R.drawable.rounded_alert_background)
        builder.setTitle("Obaveštenje")
        builder.setMessage("Potrebno je restartovati aplikaciju da bi se sačuvale promene.")
        builder.setPositiveButton("OK",
        ) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }


    fun setListeners(){
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        findViewById<ImageButton>(R.id.btnNotifications).setOnClickListener {
            when {
                //treba proveriti permisije
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU-> {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        revokeSelfPermissionOnKill(Manifest.permission.POST_NOTIFICATIONS)
                        launchInfoDialog()
                    }
                     else{
                        launchNotificationPermissionPrompt()
                    }
                }
                //treba proveriti preference
                else-> {
                    if (SharedPreferencesHelper.getValue("notifications",this)=="true") {
                        SharedPreferencesHelper.addValue("notifications","false",this@ChatActivity)
                        binding.btnNotifications.setImageResource(R.drawable.bell_off)
                    }
                    else{
                        SharedPreferencesHelper.addValue("notifications","true",this@ChatActivity)
                        binding.btnNotifications.setImageResource(R.drawable.bell_on)
                    }
                }

            }
        }
    }

    fun requestForChats(){
        MainScope().launch{
            var dbHelper= DBHelper.getInstance(this@ChatActivity)
            items=dbHelper.getContacts()
            setRecyclerView()
        }
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
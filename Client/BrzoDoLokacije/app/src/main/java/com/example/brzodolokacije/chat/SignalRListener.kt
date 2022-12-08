package com.example.brzodolokacije.chat

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.auth0.android.jwt.JWT
import com.exam.DBHelper
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Models.MessageReceive
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class SignalRListener private constructor(val activity: Activity){
    var hubConnection:HubConnection
    private var dbHelper:DBHelper
    init{
        dbHelper= DBHelper.getInstance(activity)
        hubConnection=HubConnectionBuilder.create(RetrofitHelper.baseUrl+"/chathub")
            .withHeader("access_token",SharedPreferencesHelper.getValue("jwt",activity))
            .build()
        hubConnection.keepAliveInterval=120
        hubConnection.on("Message",
            Action1 {
                    message:MessageReceive->addToDbAndloadMessageIfInChat(message,activity)
                    },
            MessageReceive::class.java
                )
        try{
            hubConnection.start().blockingAwait()
        }
        catch(e:Exception){
            Toast.makeText(activity,"Greska",Toast.LENGTH_LONG).show()
        }
        Log.d("main", hubConnection.connectionState.toString())
    }


    companion object{
        private var instance:SignalRListener?=null
        fun getInstance(activity: Activity):SignalRListener{
            if(instance==null){
                instance= SignalRListener(activity)
            }
            return instance as SignalRListener
        }

    }
    fun stopHubConnection(){
        if(hubConnection.connectionState == HubConnectionState.CONNECTED){
            hubConnection.stop()
        }
    }

    fun getConnectionState(){
        Log.d("main",hubConnection.connectionState.toString())
    }

    fun addToDbAndloadMessageIfInChat(message:MessageReceive,activity: Activity){
        var cal:Calendar= Calendar.getInstance()
        cal.time=message.timestamp
        dbHelper.addMessage(Message(message.senderId+message.timestamp,message.senderId,
        JWT(SharedPreferencesHelper.getValue("jwt",activity)!!).claims["id"]?.asString()!!,message.messagge,message.timestamp,cal),false)
        if(activity is ChatActivity){
            if(activity.clickedChat?.userId==message.senderId){
                activity.clickedChat?.requestMessages()
            }
            activity.onRefresh()
        }
        when {
            ContextCompat.checkSelfPermission(activity,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                //poslati notifikaciju
                var api=RetrofitHelper.getInstance()
                var token=SharedPreferencesHelper.getValue("jwt",activity)
                val request2=api?.getProfileFromId("Bearer "+token,
                    message.senderId
                )
                request2?.enqueue(object : retrofit2.Callback<UserReceive?> {
                    override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                        if(response.isSuccessful()){
                            var user=response.body()!!
                            createNotification(message,user,activity)
                            }
                        }

                    override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                        //TODO("Not yet implemented")
                    }
                })
                }
            }

    }

    fun createNotification(message: MessageReceive,user: UserReceive,activity: Activity){
        val notificationBuilder = NotificationCompat.Builder(activity, Notifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_round_chat_24)
            .setAutoCancel(true)
            .setLights(Color.BLUE, 500, 500)
            .setVibrate(longArrayOf(500, 500, 500))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(user.username)
            .setContentText(message.messagge)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val notificationManager = NotificationManagerCompat.from(activity)
        val notification = notificationBuilder.build()
        notificationManager.notify(NotificationID.iD, notification)
/*        Log.d("main",RetrofitHelper.baseUrl + "/api/post/image/compress/" + user.pfp!!._id)
        Glide.with(activity)
            .asBitmap()
            .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + user.pfp!!._id)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                notificationBuilder.setLargeIcon(resource)
                val notification = notificationBuilder.build()
                notificationManager.notify(NotificationID.iD, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.d("main","k")
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Log.d("main",errorDrawable.toString())
                }

            })*/
    }
    internal object NotificationID {
        private val c = AtomicInteger(100)
        val iD: Int
        get() = c.incrementAndGet()
        }

    fun log(){
        Log.d("Debug infor siganlR ", hubConnection.connectionId)
    }

}
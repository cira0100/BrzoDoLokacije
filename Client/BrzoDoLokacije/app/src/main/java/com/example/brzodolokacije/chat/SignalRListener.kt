package com.example.brzodolokacije.chat

import android.app.Activity
import android.util.Log
import com.auth0.android.jwt.JWT
import com.exam.DBHelper
import com.example.brzodolokacije.Models.Message
import com.example.brzodolokacije.Models.MessageReceive
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState


class SignalRListener private constructor(val activity: Activity){
    private var hubConnection:HubConnection
    private var dbHelper:DBHelper
    init{
        dbHelper= DBHelper.getInstance(activity)
        hubConnection=HubConnectionBuilder.create(RetrofitHelper.baseUrl+"/chathub")
            .withHeader("access_token",SharedPreferencesHelper.getValue("jwt",activity))
            .build()
        hubConnection.keepAliveInterval=120
        hubConnection.on("Message",
            Action1 {
                    message:MessageReceive->dbHelper.addMessage(Message(message.senderId+message.timestamp,message.senderId,
                JWT(SharedPreferencesHelper.getValue("jwt",activity)!!).claims["id"]?.asString()!!,message.messagge,message.timestamp),false)
                    },
            MessageReceive::class.java
                )
        hubConnection.start().blockingAwait()

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

    fun log(){
        Log.d("Debug infor siganlR ", hubConnection.connectionId)
    }

}
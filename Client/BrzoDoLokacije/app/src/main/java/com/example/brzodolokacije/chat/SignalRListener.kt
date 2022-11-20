package com.example.brzodolokacije.chat

import android.app.Activity
import android.util.Log
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single

class SignalRListener private constructor(val activity: Activity){
    private var hubConnection:HubConnection
    init{
        hubConnection=HubConnectionBuilder.create(RetrofitHelper.baseUrl+"/chathub")
            .withAccessTokenProvider(Single.defer{ Single.just(SharedPreferencesHelper.getValue("jwt", activity).toString())})
            .build()
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

}
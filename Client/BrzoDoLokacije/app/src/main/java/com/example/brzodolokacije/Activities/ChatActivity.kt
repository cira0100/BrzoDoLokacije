package com.example.brzodolokacije.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.R
import com.example.brzodolokacije.chat.DBHelper

class ChatActivity : AppCompatActivity() {

    private var dbConnection:DBHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        dbConnection= DBHelper(this@ChatActivity,null)
    }
}
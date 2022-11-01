package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.brzodolokacije.R

class ActivityForgottenPassword : AppCompatActivity() {
    private lateinit var sendCode: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        sendCode=findViewById<View>(R.id.forgottenPasswordSendCode) as Button

        sendCode.setOnClickListener{
            intent= Intent(this, ActivityForgottenPasswordVerify::class.java)
            startActivity(intent)
        }

    }


}
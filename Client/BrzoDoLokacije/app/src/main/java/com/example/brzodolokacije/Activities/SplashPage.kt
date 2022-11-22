package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.brzodolokacije.MainActivity
import com.example.brzodolokacije.R

class SplashPage : AppCompatActivity() {
    private val time:Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)

            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))

                // close this activity
                finish() }, time)
    }
}

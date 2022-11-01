package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.brzodolokacije.MainActivity
import com.example.brzodolokacije.R

class ActivityForgottenPasswordVerify : AppCompatActivity() {
    private lateinit var changePassword: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password_verify)

        changePassword=findViewById<View>(R.id.btnChangePassword) as Button
        changePassword.setOnClickListener{
            Toast.makeText(
                this, "Lozinka je uspešno promenjena.", Toast.LENGTH_LONG
            ).show();

            intent= Intent(this, ActivityLoginRegister::class.java)
            startActivity(intent)
        }
    }
}
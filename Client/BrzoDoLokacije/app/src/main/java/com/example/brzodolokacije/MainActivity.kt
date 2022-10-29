package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.Fragments.FragmentLogin
import com.example.brzodolokacije.Fragments.FragmentRegister

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent= Intent(this, ActivityLoginRegister::class.java)
        startActivity(intent)
    }
}
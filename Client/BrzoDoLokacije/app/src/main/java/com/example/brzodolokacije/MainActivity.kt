package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Services.SharedPreferencesHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent:Intent

        if(checkLoggedIn())
            intent= Intent(this, NavigationActivity::class.java)
        else
            intent= Intent(this, ActivityLoginRegister::class.java)


        startActivity(intent)
    }

    fun checkLoggedIn():Boolean{
        var jwtString=SharedPreferencesHelper.getValue("jwt",this)
        if(jwtString==null)
            return false
        var jwt:JWT=JWT(jwtString)
        if(jwt.isExpired(30))
            return false
        return true




    }
}
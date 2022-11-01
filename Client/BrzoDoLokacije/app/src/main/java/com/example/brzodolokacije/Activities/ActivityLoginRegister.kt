package com.example.brzodolokacije.Activities

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.Fragments.FragmentLogin
import com.example.brzodolokacije.Fragments.FragmentRegister
import com.example.brzodolokacije.R
import com.google.android.material.internal.ContextUtils.getActivity

class ActivityLoginRegister : AppCompatActivity() {

    private lateinit var login: Button
    private lateinit var register: Button



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        Log.d("main","123456")
        login=findViewById<View>(R.id.btnFragmentActivityLRLogin) as Button
        register=findViewById<View>(R.id.btnFragmentActivityLRRegister) as Button
        //var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
        //fm.replace(R.id.flFragmentActivityLRFragmentsView,FragmentLogin())
        var fm: FragmentTransaction =supportFragmentManager.beginTransaction()

        fm.replace(R.id.flFragmentActivityLRFragmentsView,FragmentLogin())
        fm.commit()
        login.setOnClickListener{
            login.setTextColor(getColor(R.color.white))
            register.setTextColor(getColor(R.color.teal_700))
            login.setBackgroundResource(R.drawable.rounded_cyan_button)
            register.setBackgroundResource(R.drawable.rounded_transparent_button)
            Log.d("main","prijavi se")
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentActivityLRFragmentsView,FragmentLogin())
            fm.commit()
        }

        register.setOnClickListener{
            Log.d("main","prijavi se")
            register.setTextColor(getColor(R.color.white))
            login.setTextColor(getColor(R.color.teal_700))
            register.setBackgroundResource(R.drawable.rounded_cyan_button)
            login.setBackgroundResource(R.drawable.rounded_transparent_button)
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentActivityLRFragmentsView, FragmentRegister())
            fm.commit()
        }
    }

}

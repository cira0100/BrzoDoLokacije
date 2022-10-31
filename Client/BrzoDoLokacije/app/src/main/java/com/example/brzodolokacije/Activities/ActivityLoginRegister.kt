package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
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
            Log.d("main","prijavi se")
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentActivityLRFragmentsView,FragmentLogin())
            fm.commit()
        }

        register.setOnClickListener{
            Log.d("main","prijavi se")

            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentActivityLRFragmentsView, FragmentRegister())
            fm.commit()
        }
    }

}

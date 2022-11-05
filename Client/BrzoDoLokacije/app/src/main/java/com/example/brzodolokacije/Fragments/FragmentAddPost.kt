package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.SharedPreferencesHelper


class FragmentAddPost : Fragment(R.layout.fragment_add_post) {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View=inflater.inflate(R.layout.fragment_add_post, container, false)

        return view;
    }

    fun logOut(){
        if(SharedPreferencesHelper.removeValue("jwt",requireActivity()))
        {
            val intent= Intent(requireActivity(), ActivityLoginRegister::class.java)
            startActivity(intent)
        }
    }


}
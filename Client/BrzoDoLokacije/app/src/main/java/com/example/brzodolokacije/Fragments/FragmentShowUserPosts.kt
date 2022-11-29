package com.example.brzodolokacije.Fragments

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Models.UserReceive
import com.google.gson.Gson


class FragmentShowUserPosts : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(com.example.brzodolokacije.R.layout.fragment_show_user_posts, container, false)

        return view
    }



}
package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper.baseUrl
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FragmentHomePage : Fragment() {

    private lateinit var btnChat:ImageView
    private lateinit var btnBack:ImageView
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

     }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_home_page, container, false)
        btnBack=view.findViewById(R.id.btnFragmentHomePageBack)
        btnChat=view.findViewById(R.id.ivFragmentHomePageChat)
        setBtnBackInvisible()

        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentHomePageMainScroll())
        fm.commit()

        btnBack.setOnClickListener{
            changeLocationViewToScrollView()
            setBtnBackInvisible()
        }

        btnChat.setOnClickListener {
            val intent: Intent = Intent(activity, ChatActivity::class.java)
            requireActivity().startActivity(intent)
        }

        return view
    }
    fun changeScrollVIewToLocationView(){
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentShowPostsByLocation())
        fm.commit()
    }
    fun changeLocationViewToScrollView(){
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentHomePageMainScroll())
        fm.commit()
    }
    fun setBtnBackInvisible(){
        btnBack.isVisible=false
    }
    fun setBtnBackVisible(){
        btnBack.isVisible=true
    }
}

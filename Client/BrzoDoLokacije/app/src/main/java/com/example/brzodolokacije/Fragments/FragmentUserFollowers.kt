package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentUserFollowers : Fragment() {

    private lateinit var followers:MutableList<UserReceive>
    private lateinit var searchedFollowers:MutableList<UserReceive>
    private lateinit var rvFollowers:RecyclerView
    private lateinit var userId:String
    private lateinit var showMy:String
    private lateinit var searchBar:AutoCompleteTextView
    private lateinit var searchButton:MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_user_followers, container, false)

        val bundle = arguments
        userId = bundle!!.getString("userId").toString()
        showMy = bundle!!.getString("showMy").toString().trim()
        rvFollowers=view.findViewById(R.id.rvFragmentUserFollowers)
        searchBar=view.findViewById(R.id.FragmentFollowersSearchBar)
        searchButton=view.findViewById(R.id.FragmentFollowersSearchBButton)

        if(showMy=="yes"){
            getFollowersWithoutId()
        }
        else if(showMy=="no") {
            getFollowers()
        }
        searchButton.setOnClickListener {
            searchText()
        }
        searchBar.setOnKeyListener(View.OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                searchText()
                return@OnKeyListener true
            }
            false
        })

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    searchText()
                if(count==0)
                    if(showMy=="yes"){
                        getFollowersWithoutId()
                    }
                    else if(showMy=="no") {
                        getFollowers()
                    }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        return view

    }
    fun searchText(){
        if(searchBar.text==null || searchBar.text.isNullOrEmpty() || searchBar.text.toString().trim()=="")
            return
        var text=searchBar.text.toString().trim()
        searchedFollowers= mutableListOf()
        for(user in followers){
            if(user.username.contains(text))
                searchedFollowers.add(user)
        }
        rvFollowers.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            adapter= FollowersAdapter(searchedFollowers,requireActivity())

        }




    }


    fun getFollowers(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getFollowers("Bearer "+token,userId)

        data.enqueue(object : Callback<MutableList<UserReceive>> {

            override fun onResponse(
                call: Call<MutableList<UserReceive>>,
                response: Response<MutableList<UserReceive>>
            ) {
                if (response.body() == null) {
                    return
                }
                followers = response.body()!!.toMutableList<UserReceive>()
                rvFollowers.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                    adapter= FollowersAdapter(followers,requireActivity())

                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Log.d("Followers","Faillllllllllllllllllllllllll")
                Log.d("Followers",t.toString())
            }
        })
    }

    fun getFollowersWithoutId(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getMyFollowers("Bearer "+token)

        data.enqueue(object : Callback<MutableList<UserReceive>> {
            override fun onResponse(call: Call<MutableList<UserReceive>>, response: Response<MutableList<UserReceive>>) {
                if (response.body() == null) {
                    return
                }
                Log.d("MyFollowers","Successsssssssssssssssssssssssssssss")
                followers = response.body()!!.toMutableList<UserReceive>()
                rvFollowers.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                    adapter= FollowersAdapter(followers,requireActivity())
                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Log.d("MyFollowers","Faillllllllllllllllllllllllll")
            }
        })
    }
}
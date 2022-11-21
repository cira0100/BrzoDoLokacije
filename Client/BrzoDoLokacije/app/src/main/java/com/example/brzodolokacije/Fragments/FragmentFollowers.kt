package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFollowers : Fragment() {
    private lateinit var userId:String
    private lateinit var followers: MutableList<UserReceive>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_followers, container, false)

        val bundle = this.arguments

        if (bundle != null) {
            userId= bundle.getString("userId").toString()
            Toast.makeText(
                activity, bundle.getString("userId"), Toast.LENGTH_LONG
            ).show();
        }

        getPosts()

        return view
    }

    fun getPosts(){
        val api = RetrofitHelper.getInstance()
        val data=api.getFollowers(userId)


        data.enqueue(object : Callback<MutableList<UserReceive>> {
            override fun onResponse(
                call: Call<MutableList<UserReceive>>,
                response: Response<MutableList<UserReceive>>
            ) {
                if (response.body() == null) {
                    return
                }

                followers = response.body()!!.toMutableList<UserReceive>()

            }

            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Toast.makeText(
                    activity,"nema objava", Toast.LENGTH_LONG
               ).show();
            }
        })
    }
}
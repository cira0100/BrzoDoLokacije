package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentUserFollowing : Fragment() {

    private lateinit var following:MutableList<UserReceive>
    private lateinit var rvFollowing: RecyclerView
    private lateinit var userId:String
    private lateinit var showMy:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_user_following, container, false)
        val bundle = arguments
        userId = bundle!!.getString("userId").toString()
        showMy = bundle!!.getString("showMy").toString().trim()
        rvFollowing=view.findViewById(R.id.rvFragmentUserFollowing)

        if(showMy=="yes"){
            getFollowingWithoutId()
        }
        else if(showMy=="no") {
            getFollowing()
        }
        return view
    }

    fun getFollowing(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getFollowing("Bearer "+token,userId)
        data.enqueue(object : Callback<MutableList<UserReceive>> {
            override fun onResponse(call: Call<MutableList<UserReceive>>, response: Response<MutableList<UserReceive>>) {
                if (response.body() == null) {
                    return
                }
                Log.d("Following","Successsssssssssssssssssssssssssssss")
                following = response.body()!!.toMutableList<UserReceive>()
                rvFollowing.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= FollowersAdapter(following,requireActivity())
                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Log.d("Following","Faillllllllllllllllllllllllll")
            }
        })
    }

    fun getFollowingWithoutId(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getMyFollowings("Bearer "+token)
        data.enqueue(object : Callback<MutableList<UserReceive>> {
            override fun onResponse(call: Call<MutableList<UserReceive>>, response: Response<MutableList<UserReceive>>) {
                if (response.body() == null) {
                    return
                }
                Log.d("MyFollowings","Successsssssssssssssssssssssssssssss")
                following = response.body()!!.toMutableList<UserReceive>()
                rvFollowing.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= FollowersAdapter(following,requireActivity())
                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Log.d("MyFollowings","Faillllllllllllllllllllllllll")
            }
        })
    }

}
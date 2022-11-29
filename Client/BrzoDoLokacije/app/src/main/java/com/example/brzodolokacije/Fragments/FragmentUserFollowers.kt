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
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentUserFollowers : Fragment() {

    private lateinit var followers:MutableList<UserReceive>
    private lateinit var rvFollowers:RecyclerView
    private lateinit var userId:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_user_followers, container, false)

        val bundle = arguments
        userId = bundle!!.getString("userId").toString()

        rvFollowers=view.findViewById(R.id.rvFragmentUserFollowers)

        getFollowers()

        return view
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
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= FollowersAdapter(followers,requireActivity())

                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Log.d("Followers","Faillllllllllllllllllllllllll")
                Log.d("Followers",t.toString())
            }
        })
    }
}
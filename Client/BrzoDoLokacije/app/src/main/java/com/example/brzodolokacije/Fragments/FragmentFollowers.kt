package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
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
    private lateinit var followersRv:RecyclerView
    private lateinit var btnBack:ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_followers, container, false)
        btnBack=view.findViewById(R.id.btnFragmentFollowersBack)
        followersRv=view.findViewById(R.id.rvFragmentShowFollowers)

        val bundle = this.arguments
        if (bundle != null) {
            userId= bundle.getString("userId").toString()
            Toast.makeText(
                activity, bundle.getString("userId"), Toast.LENGTH_LONG
            ).show();
        }

        //getFollowers()

        btnBack.setOnClickListener {
            val fragmentProfile = FragmentProfile()
            fragmentManager?.beginTransaction()
                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentProfile)
                ?.commit()
        }

        return view
    }

   /* fun getFollowers(){
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
                followersRv.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= FollowersAdapter(followers,requireActivity())

                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Toast.makeText(
                    activity,"nema pratilaca", Toast.LENGTH_LONG
               ).show();
            }
        })
    }*/
}
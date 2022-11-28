package com.example.brzodolokacije.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentFollowing : Fragment() {
    private lateinit var userId:String
    private lateinit var following: MutableList<UserReceive>
    private lateinit var followingRv: RecyclerView
    private lateinit var back:ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_following, container, false)

        followingRv=view.findViewById(R.id.rvFragmentShowFollowing)
        back=view.findViewById(R.id.btnFragmentFollowingBack)
        val bundle = this.arguments
        if (bundle != null) {
            userId= bundle.getString("userId").toString()
            Toast.makeText(
                activity, bundle.getString("userId"), Toast.LENGTH_LONG
            ).show();
        }

        //getFollowing()

        back.setOnClickListener {
            val fragmentProfile = FragmentProfile()
            fragmentManager
                ?.beginTransaction()
                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentProfile)
                ?.commit()
        }
        return view
    }

    /*fun getFollowing(){
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
                following = response.body()!!.toMutableList<UserReceive>()
                followingRv.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= FollowersAdapter(following,requireActivity())

                }
            }
            override fun onFailure(call: Call<MutableList<UserReceive>>, t: Throwable) {
                Toast.makeText(
                    activity,"nema pracenja", Toast.LENGTH_LONG
                ).show();
            }
        })
    }*/
}
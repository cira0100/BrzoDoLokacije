package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProfile : Fragment(R.layout.fragment_profile) {
    // TODO: Rename and change types of parameters
    private lateinit var username: TextView
    private lateinit var name: TextView
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount:TextView
    private lateinit var usernameString: String
    private lateinit var nameString: String
    private lateinit var postsCountString: String
    private lateinit var followersCountString: String
    private lateinit var followingCountString:String
    private lateinit var showMyPosts: Button
    private lateinit var showMyData: Button
    private lateinit var showMyRecensions: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_profile, container, false)
        name = view.findViewById<View>(R.id.tvFragmentProfileName) as TextView
        username = view.findViewById<View>(R.id.tvFragmentProfileUserName) as TextView
        postsCount = view.findViewById<View>(R.id.tvFragmentProfilePostsNo) as TextView
        followersCount = view.findViewById<View>(R.id.tvFragmentProfileFollowersNo) as TextView
        followingCount = view.findViewById<View>(R.id.tvFragmentProfileFollowNo) as TextView

        showMyPosts=view.findViewById<View>(R.id.btnFragmentProfileShowMyPosts) as Button
        showMyData=view.findViewById<View>(R.id.btnFragmentProfileShowMyData) as Button
        showMyRecensions=view.findViewById<View>(R.id.btnFragmentProfileShowMyRecensions) as Button
        //podaci iz baze



        showMyPosts.setOnClickListener{

            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentProfileFragmentContainer, FragmentUserPosts())
            fm.commit()
        }


        showMyData.setOnClickListener{

            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentProfileFragmentContainer, FragmentMyProfileInfo())
            fm.commit()
        }

        showMyRecensions.setOnClickListener{

            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentProfileFragmentContainer, FragmentMyRecensions())
            fm.commit()
        }

        return view
    }


}
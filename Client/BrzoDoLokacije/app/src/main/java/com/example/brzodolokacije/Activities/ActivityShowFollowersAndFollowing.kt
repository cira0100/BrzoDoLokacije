package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.Fragments.*
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item.*
import retrofit2.Call
import retrofit2.Response

class ActivityShowFollowersAndFollowing : AppCompatActivity() {

   // private lateinit var showFollowers:Button
    //private lateinit var showFollowing:Button
    private lateinit var fragmentContainer:FrameLayout
    private lateinit var followersOrFollowing:String
    private lateinit var userId:String
    private lateinit var text:TextView
    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_followers_and_following)

        val bundle = intent.extras
        if (bundle != null){
            userId= bundle.getString("userId").toString().trim()
            followersOrFollowing=bundle.get("show").toString().trim()
        }

        fragmentContainer=findViewById(R.id.flActivityShowFollowerAndFollowing)
        text=findViewById(R.id.tvActivityShowFollowersOrFollowingShow)
        back=findViewById(R.id.btnActivityShowFollowersAndFollowingBackToUser)

        if(followersOrFollowing=="followers"){
            text.text="Pratioci"
            val mFragmentManager = supportFragmentManager
            val mFragmentTransaction = mFragmentManager.beginTransaction()
            val mFragment = FragmentUserFollowers()
            val mBundle = Bundle()
            mBundle.putString("userId",userId)
            mFragment.arguments = mBundle
            mFragmentTransaction.replace(R.id.flActivityShowFollowerAndFollowing, mFragment).commit()
        }

        if(followersOrFollowing=="following"){
            text.text="Praćenja"
            val mFragmentManager = supportFragmentManager
            val mFragmentTransaction = mFragmentManager.beginTransaction()
            val mFragment = FragmentUserFollowing()
            val mBundle = Bundle()
            mBundle.putString("userId",userId)
            mFragment.arguments = mBundle
            mFragmentTransaction.replace(R.id.flActivityShowFollowerAndFollowing, mFragment).commit()
        }

        back.setOnClickListener {
            finish()


        }


/*
        showFollowers=findViewById(R.id.btnActivityShowFollowersAndFollowingShowFollowers)
        showFollowing=findViewById(R.id.btnActivityShowFollowersAndFollowingShowFollowing)
        fragmentContainer=findViewById(R.id.flActivityShowFollowerAndFollowing)

        showFollowers.setOnClickListener {
            followersOrFollowing="followers"
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            fm.replace(R.id.flActivityShowFollowerAndFollowing, FragmentUserFollowers())
            fm.commit()
        }

        showFollowing.setOnClickListener {
            followersOrFollowing="following"
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            fm.replace(R.id.flActivityShowFollowerAndFollowing, FragmentUserFollowing())
            fm.commit()
        }
*/
    }
}
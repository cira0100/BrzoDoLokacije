package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Fragments.FragmentShowUserPosts
import com.example.brzodolokacije.Models.User
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.google.gson.Gson

class ActivityUserProfile : AppCompatActivity() {
    private lateinit var name:TextView
    private lateinit var postsNumber:TextView
    private lateinit var followersNumber:TextView
    private lateinit var followingNumber:TextView
    private lateinit var profilePicture:ImageView
    private lateinit var followUser: ImageButton
    private lateinit var showUserPosts: Button
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var myObject:UserReceive
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        name=findViewById(R.id.tvActivityUserProfileName)
        postsNumber=findViewById(R.id.tvActivityUserProfilePostsNo)
        followersNumber=findViewById(R.id.tvActivityUserProfileFollowersNo)
        followingNumber=findViewById(R.id.tvActivityUserProfileFollowNo)
        profilePicture=findViewById(R.id.tvActivityProfileProfilePicture)
        followUser=findViewById(R.id.ibActivityUserProfileFollow)
        showUserPosts=findViewById(R.id.btnActivityUserProfileShowPosts)
        fragmentContainer=findViewById(R.id.flActivityProfileFragmentContainer)

        val jsonMyObject: String
        val extras = intent.extras
        if (extras != null) {
            jsonMyObject = extras.getString("user")!!
            myObject= Gson().fromJson(jsonMyObject, UserReceive::class.java)

            name.text=myObject.name
            postsNumber.text=myObject.postNumber.toString()
            followersNumber.text=myObject.followers.toString()
            followingNumber.text=myObject.following.toString()

            if(myObject.pfp!=null) {
                Glide.with(this)
                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + myObject.pfp!!._id)
                    .circleCrop()//Round image
                    .into(profilePicture)
            }
        }

        followUser.setOnClickListener{

        }

        showUserPosts.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user", Gson().toJson(myObject))
            val fragment = Fragment()
            fragment.arguments = bundle
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.flActivityProfileFragmentContainer,FragmentShowUserPosts()).commit()

        }
    }

}
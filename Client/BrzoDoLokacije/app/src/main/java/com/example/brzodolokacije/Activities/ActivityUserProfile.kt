package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        name=findViewById(R.id.tvActivityUserProfileName)
        postsNumber=findViewById(R.id.tvActivityUserProfilePostsNo)
        followersNumber=findViewById(R.id.tvActivityUserProfileFollowersNo)
        followingNumber=findViewById(R.id.tvActivityUserProfileFollowNo)
        profilePicture=findViewById(R.id.tvActivityProfileProfilePicture)
        val jsonMyObject: String
        val extras = intent.extras
        if (extras != null) {
            jsonMyObject = extras.getString("user")!!
            val myObject: UserReceive = Gson().fromJson(jsonMyObject, UserReceive::class.java)

            name.text=myObject.name
            postsNumber.text=myObject.postNumber.toString()
            followersNumber.text="0"
            followingNumber.text="0"

            if(myObject.pfp!=null) {
                Glide.with(this)
                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + myObject.pfp!!._id)
                    .circleCrop()//Round image
                    .into(profilePicture)
            }
        }

    }
}
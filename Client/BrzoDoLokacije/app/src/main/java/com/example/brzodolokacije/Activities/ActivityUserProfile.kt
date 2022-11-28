package com.example.brzodolokacije.Activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Fragments.FragmentShowUserPosts
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.R.*
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext


class ActivityUserProfile : AppCompatActivity() {
    private lateinit var name:TextView
    private lateinit var postsNumber:TextView
    private lateinit var followersNumber:TextView
    private lateinit var followingNumber:TextView
    private lateinit var profilePicture:ImageView
    private lateinit var followUser: Button
    private lateinit var showUserPosts: Button
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var userObject:UserReceive
    private lateinit var openChat:ImageButton

    private lateinit var followersList: MutableList<UserReceive>
    private lateinit var followingList: MutableList<UserReceive>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_user_profile)

        name=findViewById(id.tvActivityUserProfileName)
        postsNumber=findViewById(id.tvActivityUserProfilePostsNo)
        followersNumber=findViewById(id.tvActivityUserProfileFollowersNo)
        followingNumber=findViewById(id.tvActivityUserProfileFollowNo)
        profilePicture=findViewById(id.tvActivityProfileProfilePicture)
        followUser=findViewById(id.btnActivityUserProfileFollow)
        showUserPosts=findViewById(id.btnActivityUserProfileShowPosts)
        fragmentContainer=findViewById(id.flActivityProfileFragmentContainer)
        openChat=findViewById(id.activityUserProfileOpenChat)

        val jsonMyObject: String
        val extras = intent.extras
        if (extras != null) {
            jsonMyObject = extras.getString("user")!!
            //val myObject: UserReceive = Gson().fromJson(jsonMyObject, UserReceive::class.java)

            userObject= Gson().fromJson(jsonMyObject, UserReceive::class.java)

            name.text=userObject.name
            postsNumber.text=userObject.postNumber.toString()
            followersNumber.text=userObject?.followersCount.toString()
            followingNumber.text=userObject?.followingCount.toString()

            if(userObject.pfp!=null) {
                Glide.with(this)
                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + userObject.pfp!!._id)
                    .circleCrop()//Round image
                    .into(profilePicture)
            }
        }

        checkIfAlreadyFollow()

        followUser.setOnClickListener{

            checkIfAlreadyFollow()


            val api = RetrofitHelper.getInstance()
            val token= SharedPreferencesHelper.getValue("jwt", this@ActivityUserProfile)
            var data=api.addFollower("Bearer "+token,userObject._id);
            data.enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    Toast.makeText(
                        this@ActivityUserProfile, "PRATITE KORISNIKA", Toast.LENGTH_LONG
                    ).show();
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(
                        this@ActivityUserProfile, t.toString(), Toast.LENGTH_LONG
                    ).show();
                }
            })

        }

        showUserPosts.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user", Gson().toJson(userObject))
            val fragment = Fragment()
            fragment.arguments = bundle
            getSupportFragmentManager().beginTransaction()
                .replace(id.flActivityProfileFragmentContainer,FragmentShowUserPosts()).commit()

        }
    }

    fun checkIfAlreadyFollow(){
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivityUserProfile)
        var data=api.checkIfAlreadyFollow("Bearer "+token,userObject._id);
        data.enqueue(object : Callback<Boolean> {

            override fun onFailure(call: Call<Boolean>, t: Throwable) {;
                Log.d("fail","faillllllllllllllllllllllllllllllllllllllllllllllllllllllll")
                Log.d("fail",t.toString())
            }

            @SuppressLint("ResourceAsColor")
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d("success","successsssssssssssssssssss")
                if (response.body() == null) {
                    return
                }
                var follow = response.body()!!
                if(follow){
                    Log.d("success","follow")
                    followUser.setCompoundDrawablesWithIntrinsicBounds(drawable.ic_outline_person_remove_24,0,0,0)
                    followUser.text="Ne prati više"

                }
                if(!follow){
                    Log.d("success","not follow")
                    followUser.setCompoundDrawablesWithIntrinsicBounds(drawable.ic_outline_person_add_alt_24,0,0,0)
                    followUser.text="Prati"
                }

            }
        })

    }

    fun checkIfAlreadyFollowChangeButton(){

    }
}
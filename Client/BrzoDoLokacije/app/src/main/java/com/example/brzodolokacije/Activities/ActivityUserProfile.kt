package com.example.brzodolokacije.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Fragments.FragmentUserPostsProfileActivity
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.R.*
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    private lateinit var unfollowUser:Button

    private lateinit var showFollowers:Button
    private lateinit var showFollowing:Button

    private var follow:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_user_profile)

        name=findViewById(id.tvActivityUserProfileName)
        postsNumber=findViewById(id.tvActivityUserProfilePostsNo)
        followersNumber=findViewById(id.tvActivityUserProfileFollowersNo)
        followingNumber=findViewById(id.tvActivityUserProfileFollowNo)
        profilePicture=findViewById(id.tvActivityProfileProfilePicture)
        followUser=findViewById(id.btnActivityUserProfileFollow)
        unfollowUser=findViewById(id.btnActivityUserProfileUnFollow)
        showUserPosts=findViewById(id.btnActivityUserProfileShowPosts)
        fragmentContainer=findViewById(id.flActivityProfileFragmentContainer)
        openChat=findViewById(id.activityUserProfileOpenChat)
        showFollowing=findViewById(id.tvActivityUserProfileFollow)
        showFollowers=findViewById(R.id.tvActivityUserProfileFollowers)

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
                Glide.with(this@ActivityUserProfile)
                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + userObject.pfp!!._id)
                    .circleCrop()//Round image
                    .into(profilePicture)
            }
        }


        checkIfAlreadyFollow()
        updateUserData()

        followUser.setOnClickListener{
                val api = RetrofitHelper.getInstance()
                val token = SharedPreferencesHelper.getValue("jwt", this@ActivityUserProfile)
                var data = api.addFollower("Bearer " + token, userObject._id);
                data.enqueue(object : Callback<Boolean> {
                    override fun onResponse(
                        call: Call<Boolean>,
                        response: Response<Boolean>
                    ) {
                        unfollowUser.isVisible=true
                        unfollowUser.isClickable=true
                        unfollowUser.isEnabled=true
                        followUser.isVisible=false
                        followUser.isClickable=false
                        followUser.isEnabled=false

                        updateUserData()

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
        unfollowUser.setOnClickListener {
            val api = RetrofitHelper.getInstance()
            val token = SharedPreferencesHelper.getValue("jwt", this@ActivityUserProfile)
            var data = api.unfollow("Bearer " + token, userObject._id);
            data.enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    unfollowUser.isVisible = false
                    unfollowUser.isClickable = false
                    unfollowUser.isEnabled = false
                    followUser.isVisible = true
                    followUser.isClickable = true
                    followUser.isEnabled = true
                    updateUserData()
                    Toast.makeText(
                        this@ActivityUserProfile, "VIŠE NE PRATITE KORISNIKA", Toast.LENGTH_LONG
                    ).show();
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(
                        this@ActivityUserProfile, t.toString(), Toast.LENGTH_LONG
                    ).show();
                }
            })
        }

        showFollowers.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId", userObject._id.toString())
            bundle.putString("show","followers")
            bundle.putString("showMy","no")
            val intent = Intent(this@ActivityUserProfile,ActivityShowFollowersAndFollowing::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }

        showFollowing.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId", userObject._id.toString())
            bundle.putString("show","following")
            bundle.putString("showMy","no")
            val intent = Intent(this@ActivityUserProfile,ActivityShowFollowersAndFollowing::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }



        showUserPosts.setOnClickListener {
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            val fragment = FragmentUserPostsProfileActivity()
            val b = Bundle()
            b.putString("userId", userObject._id.toString())
            fragment.arguments = b
            fm.replace(R.id.flActivityProfileFragmentContainer, fragment)
            fm.commit()
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
                    /*followUser.setCompoundDrawablesWithIntrinsicBounds(drawable.ic_outline_person_remove_24,0,0,0)
                    followUser.text="Ne prati više"
                    follow=false
                    */
                    unfollowUser.isVisible=true
                    unfollowUser.isClickable=true
                    unfollowUser.isEnabled=true
                    followUser.isVisible=false
                    followUser.isClickable=false
                    followUser.isEnabled=false
                }
                else{
                    Log.d("success","not follow")
                    /*followUser.setCompoundDrawablesWithIntrinsicBounds(drawable.ic_outline_person_add_alt_24,0,0,0)
                    followUser.text="Prati"
                    follow=true
                    */

                    unfollowUser.isVisible=false
                    unfollowUser.isClickable=false
                    unfollowUser.isEnabled=false
                    followUser.isVisible=true
                    followUser.isClickable=true
                    followUser.isEnabled=true


                }

            }
        })

    }

    fun updateUserData(){
        val api = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", this@ActivityUserProfile)
        var data = api.getProfileFromId("Bearer " + token, userObject._id);
        data.enqueue(object : Callback<UserReceive> {
            override fun onResponse(
                call: Call<UserReceive>,
                response: Response<UserReceive>
            ) {
                var userData=response.body()!!

                name.text=userData.name
                postsNumber.text=userData.postNumber.toString()
                followersNumber.text=userData.followersCount.toString()
                followingNumber.text=userData.followingCount.toString()
            }

            override fun onFailure(call: Call<UserReceive>, t: Throwable) {
                Toast.makeText(
                    this@ActivityUserProfile, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })
    }

}
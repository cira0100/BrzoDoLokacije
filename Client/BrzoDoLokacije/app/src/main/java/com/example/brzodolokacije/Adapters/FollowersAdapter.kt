package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivityUserProfile
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.google.gson.Gson

class FollowersAdapter (var followers:MutableList<UserReceive>, val activity: Activity):
    RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersAdapter.FollowerViewHolder {
            val view= LayoutInflater.from(parent.context).inflate(R.layout.follower_item,parent,false)
            return FollowerViewHolder(view)
        }

        override fun onBindViewHolder(holder: FollowersAdapter.FollowerViewHolder, position: Int) {
            return holder.bindView(followers[position] )
        }

        override fun getItemCount(): Int {
            return followers.size
        }


        inner class FollowerViewHolder(view: View): RecyclerView.ViewHolder(view){

            private val name: TextView =view.findViewById(R.id.tvFollowerItemName)
            private val username:TextView=view.findViewById(R.id.tvFollowerItemUsername)
            private val pfp: ImageView =view.findViewById(R.id.tvFragmentProfileProfilePicture)
            fun bindView(follower: UserReceive){
                name.text=follower.name
                username.text="@"+follower.username
                if(follower.pfp!=null) {
                    Glide.with(activity)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + follower.pfp!!._id)
                        .circleCrop()
                        .into(pfp)
                }
                itemView.setOnClickListener {
                    val intent: Intent = Intent(activity, ActivityUserProfile::class.java)
                    var b= Bundle()
                    intent.putExtra("user", Gson().toJson(follower))
                    activity.startActivity(intent)
                }
            }
        }
}
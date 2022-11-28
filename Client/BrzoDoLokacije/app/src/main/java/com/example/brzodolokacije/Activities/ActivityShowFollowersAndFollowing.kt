package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.Fragments.FragmentFollowers
import com.example.brzodolokacije.Fragments.FragmentFollowing
import com.example.brzodolokacije.Fragments.FragmentRegister
import com.example.brzodolokacije.R

class ActivityShowFollowersAndFollowing : AppCompatActivity() {

    private lateinit var showFollowers:Button
    private lateinit var showFollowing:Button
    private lateinit var fragmentContainer:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_followers_and_following)

        showFollowers=findViewById(R.id.btnActivityShowFollowersAndFollowingShowFollowers)
        showFollowing=findViewById(R.id.btnActivityShowFollowersAndFollowingShowFollowing)
        fragmentContainer=findViewById(R.id.flActivityShowFollowerAndFollowing)

        showFollowers.setOnClickListener {
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            fm.replace(R.id.flActivityShowFollowerAndFollowing, FragmentFollowers())
            fm.commit()
        }

        showFollowing.setOnClickListener {
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            fm.replace(R.id.flActivityShowFollowerAndFollowing, FragmentFollowing())
            fm.commit()
        }

    }
}
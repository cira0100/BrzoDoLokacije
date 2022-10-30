package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments.FragmentAddPost
import com.example.brzodolokacije.Fragments.FragmentBrowse
import com.example.brzodolokacije.Fragments.FragmentHome
import com.example.brzodolokacije.Fragments.FragmentProfile
import com.example.brzodolokacije.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val homeFragment=FragmentHome()
        val browseFragment=FragmentBrowse()
        val addPostFragment=FragmentAddPost()
        val profileFragment=FragmentProfile()
        val bottomNav=findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        setCurrentFragment(homeFragment)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome->setCurrentFragment(homeFragment)
                R.id.navAddPost->setCurrentFragment(addPostFragment)
                R.id.navBrowse->setCurrentFragment(browseFragment)
                R.id.navProfile->setCurrentFragment(profileFragment)

            }
            true
        }


    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flNavigationFragment,fragment)
            commit()
        }


}
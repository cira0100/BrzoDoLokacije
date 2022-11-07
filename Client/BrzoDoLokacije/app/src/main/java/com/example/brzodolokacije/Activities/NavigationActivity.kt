package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments.*
import com.example.brzodolokacije.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class NavigationActivity : AppCompatActivity() {

    //lateinit var openAddPost:Button
    //lateinit var capturePost:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val fragmentShowPosts=FragmentShowPosts()
        val browseFragment=FragmentBrowse()
        val addPostFragment= FragmentAddNew()
        val profileFragment=FragmentProfile()
        val bottomNav=findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        setCurrentFragment(fragmentShowPosts)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome->setCurrentFragment(fragmentShowPosts)
                //R.id.navAddPost->setCurrentFragment(addPostFragment)
                R.id.navAddPost->showBottomSheetAddNew()
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

    private fun showBottomSheetAddNew(){
        var bottomSheetDialog:BottomSheetDialog
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_new, null)
        bottomSheetDialog=BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_new)
        bottomSheetDialog.show()


        var openAddPost=bottomSheetDialog.findViewById<View>(R.id.btnBottomSheetAddNewOpenAddPost) as Button
        var capturePost=bottomSheetDialog.findViewById<View>(R.id.btnBottomSheetAddNewOpenCapturePost) as Button

        openAddPost.setOnClickListener{
            Toast.makeText(
                applicationContext, "Open ", Toast.LENGTH_LONG
            ).show();
            val intent = Intent (this, ActivityAddPost::class.java)
            startActivity(intent)
        }

    }


}
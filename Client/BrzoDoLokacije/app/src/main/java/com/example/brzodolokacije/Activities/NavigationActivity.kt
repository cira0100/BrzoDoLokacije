package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
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
        Toast.makeText(
            applicationContext, "Open  ", Toast.LENGTH_LONG
        ).show();
        val fragmentHomePage=FragmentHomePage()
        val fragmentShowPosts=FragmentShowPosts()
        val browseFragment=FragmentBrowse()
        val addPostFragment= FragmentAddNew()
        val profileFragment=FragmentProfile()
        val bottomNav=findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        setCurrentFragment(fragmentHomePage)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHomePage->setCurrentFragment(fragmentHomePage)
                R.id.navAllPosts->setCurrentFragment(fragmentShowPosts)
                //R.id.navAddPost->setCurrentFragment(addPostFragment)
                R.id.navAddPost->showBottomSheetAddPost()
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

    private fun showBottomSheetAddPost(){
        var bottomSheetDialog2:BottomSheetDialog
        bottomSheetDialog2=BottomSheetDialog(this)
        bottomSheetDialog2.setContentView(R.layout.bottom_sheet_add_new_post)
        bottomSheetDialog2.show()

        var close=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostClose) as ImageButton
        var openAddPost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenAddPost) as ImageButton
        var capturePost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenCapturePost) as ImageButton

        openAddPost.setOnClickListener{
            Toast.makeText(
                applicationContext, "Open select from gallery ", Toast.LENGTH_LONG
            ).show();
            val intent = Intent (this, ActivityAddPost::class.java)
            startActivity(intent)
        }

        capturePost.setOnClickListener{
            Toast.makeText(
                applicationContext, "Open capture ", Toast.LENGTH_LONG
            ).show();
            val intent = Intent (this, ActivityCapturePost::class.java)
            startActivity(intent)
        }
        close.setOnClickListener {
            bottomSheetDialog2.dismiss()
        }

    }
    private fun showBottomSheetAddNew(){
        Toast.makeText(
            applicationContext, "Open add new ", Toast.LENGTH_LONG
        ).show();
        var bottomSheetDialog:BottomSheetDialog
        bottomSheetDialog=BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_new)
        bottomSheetDialog.show()
        var close=bottomSheetDialog.findViewById<View>(R.id.btnBottomSheetAddNewClose) as ImageButton
        var newPost=bottomSheetDialog.findViewById<View>(R.id.btnBottomSheetAddNewPost) as ImageButton
        var newLocation=bottomSheetDialog.findViewById<View>(R.id.btnBottomSheetAddNewLocation) as ImageButton

        newPost.setOnClickListener{
            bottomSheetDialog.dismiss()
            showBottomSheetAddPost()
        }

        newLocation.setOnClickListener{
            Toast.makeText(
                applicationContext, "Open capture ", Toast.LENGTH_LONG
            ).show();
            val intent = Intent (this, MapsActivity::class.java)
            startActivity(intent)
        }

        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }


}
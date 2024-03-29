package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments.*
import com.example.brzodolokacije.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class NavigationActivity : AppCompatActivity() {

    //lateinit var openAddPost:Button
    //lateinit var capturePost:Button
    public lateinit var bottomNav:BottomNavigationView
    public lateinit var searchQuery:String
    public lateinit var searchId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        searchQuery=""
        searchId=""
        val fragmentHomePage=FragmentHomePage()
        val fragmentShowPosts=FragmentShowPosts()
        val browseFragment=FragmentBrowse()
        val addPostFragment= FragmentAddNew()
        val profileFragment=FragmentProfile()
        bottomNav=findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        setCurrentFragment(fragmentHomePage)
        KeyboardEvents()
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


        var openAddPost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenAddPost) as Button
        var capturePost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenCapturePost) as Button

        openAddPost.setOnClickListener{
            val intent = Intent (this, ActivityAddPost::class.java)
            startActivity(intent)
        }

        capturePost.setOnClickListener{

            val intent = Intent (this, ActivityCapturePost::class.java)
            startActivity(intent)
        }


    }
    private fun showBottomSheetAddNew(){

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

            val intent = Intent (this, MapsActivity::class.java)
            startActivity(intent)
        }


    }

    fun KeyboardEvents(){
        KeyboardVisibilityEvent.setEventListener(
            this
        ) { isOpen ->
                if (isOpen) {
                    bottomNav.visibility = View.GONE
                    bottomNav.forceLayout()

                } else {
                    MainScope().launch {
                        bottomNav.visibility = View.VISIBLE
                        bottomNav.forceLayout()
                    }
                }
        }
    }

    fun changeToProfile(){
        setCurrentFragment(FragmentProfile())
        bottomNav.menu.findItem(R.id.navProfile).isChecked = true
    }

}
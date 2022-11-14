package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ActivityCapturePost
import com.example.brzodolokacije.Activities.ActivityForgottenPassword
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentUserPosts : Fragment() {
    private lateinit var addNewPost: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_user_posts, container, false)
//        Toast.makeText(
//            activity, "****************USER*****************", Toast.LENGTH_LONG
//        ).show();
        addNewPost=view.findViewById<View>(R.id.tvFragmentUserPostsAddPost) as TextView
        addNewPost.setOnClickListener {
            var bottomSheetDialog2: BottomSheetDialog
            bottomSheetDialog2= BottomSheetDialog(requireContext())
            bottomSheetDialog2.setContentView(R.layout.bottom_sheet_add_new_post)
            bottomSheetDialog2.show()

            var close=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostClose) as ImageButton
            var openAddPost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenAddPost) as ImageButton
            var capturePost=bottomSheetDialog2.findViewById<View>(R.id.btnBottomSheetAddNewPostOpenCapturePost) as ImageButton

            openAddPost.setOnClickListener{
                val intent = Intent (getActivity(), ActivityAddPost::class.java)
                getActivity()?.startActivity(intent)
            }

            capturePost.setOnClickListener{
                val intent = Intent (getActivity(), ActivityCapturePost::class.java)
                getActivity()?.startActivity(intent)
            }
            close.setOnClickListener {
                bottomSheetDialog2.dismiss()
            }
        }

        return view
    }

}
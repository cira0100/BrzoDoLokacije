package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ActivityForgottenPassword
import com.example.brzodolokacije.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddNew.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddNew : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var addNewPost: Button
    private lateinit var addNewLocation: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View=inflater.inflate(R.layout.fragment_add_new, container, false)
        addNewPost=view.findViewById<View>(R.id.btnFragmentAddNewNewPost) as Button
        addNewLocation=view.findViewById<View>(R.id.btnFragmentAddNewNewLocation) as Button


        addNewPost.setOnClickListener{

                val intent = Intent (getActivity(), ActivityAddPost::class.java)
                getActivity()?.startActivity(intent)

/*
            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentAddNewFragmentContainer, FragmentAddPost())
            fm.commit()*/
        }


        addNewLocation.setOnClickListener{

            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(R.id.flFragmentAddNewFragmentContainer, FragmentAddLocation())
            fm.commit()
        }

        return view
    }


}
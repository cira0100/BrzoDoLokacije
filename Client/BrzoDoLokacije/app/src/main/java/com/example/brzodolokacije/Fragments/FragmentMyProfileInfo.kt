package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.exam.DBHelper
import com.example.brzodolokacije.Activities.ActivityChangeUserData
import com.example.brzodolokacije.Activities.ActivityForgottenPassword
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.SharedPreferencesHelper


class FragmentMyProfileInfo : Fragment() {
    private lateinit var logout:Button
    private lateinit var changeAccount:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_my_profile_info, container, false)

        logout=view.findViewById<View>(R.id.buttonLogOut) as Button
        changeAccount=view.findViewById(R.id.changeAccountData)

        logout.setOnClickListener{
            logOut()
        }

        changeAccount.setOnClickListener {
            val intent = Intent (getActivity(), ActivityChangeUserData::class.java)
            getActivity()?.startActivity(intent)
        }

        return view
    }

    fun logOut(){
        if(SharedPreferencesHelper.removeValue("jwt",requireActivity()))
        {
            DBHelper.getInstance(requireActivity()).deleteDB();
            val intent= Intent(requireActivity(), ActivityLoginRegister::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
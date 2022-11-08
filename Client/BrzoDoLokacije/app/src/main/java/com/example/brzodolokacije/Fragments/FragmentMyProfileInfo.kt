package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.SharedPreferencesHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMyProfileInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMyProfileInfo : Fragment() {
    private lateinit var logout:Button
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_my_profile_info, container, false)

        logout=view.findViewById<View>(R.id.buttonLogOut) as Button
        logout.setOnClickListener{
            logOut()

        }

        return view
    }

    fun logOut(){
        if(SharedPreferencesHelper.removeValue("jwt",requireActivity()))
        {
            val intent= Intent(requireActivity(), ActivityLoginRegister::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
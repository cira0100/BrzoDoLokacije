package com.example.brzodolokacije.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.exam.DBHelper
import com.example.brzodolokacije.Activities.ActivityChangeUserData
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.FragmentProfileStatistics
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.SharedPreferencesHelper


class FragmentMyProfileInfo : Fragment() {
    private lateinit var logout:Button
    private lateinit var changeAccount:Button
    private lateinit var statistics:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_my_profile_info, container, false)

        logout=view.findViewById<View>(R.id.buttonLogOut) as Button
        changeAccount=view.findViewById(R.id.changeAccountData)
        statistics=view.findViewById<View>(R.id.getAccoutStatistics) as Button

        logout.setOnClickListener{
            logOut()
        }

        changeAccount.setOnClickListener {
            val intent = Intent (getActivity(), ActivityChangeUserData::class.java)
            getActivity()?.startActivity(intent)
        }
        statistics.setOnClickListener {

            val manager: androidx.fragment.app.FragmentManager? = fragmentManager
            val transaction: FragmentTransaction = manager!!.beginTransaction()
            transaction.replace(R.id.flFragmentProfileFragmentContainer, FragmentProfileStatistics())
            transaction.commit()





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
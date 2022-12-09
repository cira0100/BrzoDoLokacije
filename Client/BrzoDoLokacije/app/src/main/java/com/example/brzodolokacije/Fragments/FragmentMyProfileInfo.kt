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
import com.example.brzodolokacije.Activities.ActivityChangePassword
import com.example.brzodolokacije.Activities.ActivityChangeUserData
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.FragmentProfileStatistics
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.SharedPreferencesHelper


class FragmentMyProfileInfo : Fragment() {
    private lateinit var logout:Button
    private lateinit var changeAccount:Button
    private lateinit var statistics:Button
    private lateinit var changePassword:Button
    private lateinit var favouritePosts:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_my_profile_info, container, false)

        logout=view.findViewById<View>(R.id.buttonLogOut) as Button
        changeAccount=view.findViewById(R.id.changeAccountData)
        statistics=view.findViewById<View>(R.id.getAccoutStatistics) as Button
        changePassword=view.findViewById(R.id.ChangePassword)
        favouritePosts=view.findViewById(R.id.getMyFavorite)
        logout.setOnClickListener{
            logOut()
        }

        changeAccount.setOnClickListener {
            val intent = Intent (getActivity(), ActivityChangeUserData::class.java)
            getActivity()?.startActivity(intent)
        }

        favouritePosts.setOnClickListener {

            val manager: androidx.fragment.app.FragmentManager? = fragmentManager
            val transaction: FragmentTransaction = manager!!.beginTransaction()

            var fragment:FragmentUserPosts=FragmentUserPosts()
            val bundle = Bundle()
            var parentFragment:FragmentProfile=parentFragment as FragmentProfile
            var username=parentFragment.usernameStringSend
            bundle.putString("username", username)
            fragment.arguments=bundle



            transaction.replace(R.id.flFragmentProfileFragmentContainer,fragment )
            transaction.commit()

        }
        statistics.setOnClickListener {

            val manager: androidx.fragment.app.FragmentManager? = fragmentManager
            val transaction: FragmentTransaction = manager!!.beginTransaction()

            var fragment:FragmentProfileStatistics=FragmentProfileStatistics()
            val bundle = Bundle()
            var parentFragment:FragmentProfile=parentFragment as FragmentProfile
            var username=parentFragment.usernameStringSend
            bundle.putString("username", username)
            fragment.arguments=bundle



            transaction.replace(R.id.flFragmentProfileFragmentContainer,fragment )
            transaction.commit()

        }


        changePassword.setOnClickListener {
            val intent = Intent (getActivity(), ActivityChangePassword::class.java)
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
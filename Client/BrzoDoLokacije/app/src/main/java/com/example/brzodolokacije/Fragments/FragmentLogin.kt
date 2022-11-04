package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.Activities.NavigationActivity

import com.example.brzodolokacije.Activities.ActivityForgottenPassword


import com.example.brzodolokacije.Interfaces.IAuthApi
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Response


class FragmentLogin : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var forgottenPassword: TextView
    private lateinit var login: Button
    private lateinit var passwordString:String
    private lateinit var emailString:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View=inflater.inflate(R.layout.fragment_login, container, false)


        email = view.findViewById<View>(R.id.etFragmentLoginEmail) as EditText
        password = view.findViewById<View>(R.id.etFragmentLoginPassword) as EditText
        forgottenPassword = view.findViewById<View>(R.id.tvFragmentLoginForgottenPassword) as TextView
        login=view.findViewById<View>(R.id.btnFragmentLoginLogin) as Button

        //osluskivanje unosa

        login.setOnClickListener{
            emailString=email.text.toString().trim()
            passwordString=password.text.toString().trim()
            //prazan unos?
            if(emailString.isEmpty())
            {
                email.hint="Unesite Email adresu"
                email.setHintTextColor(Color.RED)
            }/*
            else{
                if(checkEmail(emailString)==false){
                    email.hint="Pogrešan unos, unesite ispravnu Email adresu"
                    email.setHintTextColor(Color.RED)
                }
            }*/
            if(passwordString.isEmpty())
            {
                password.hint = "Unesite lozinku"
                password.setHintTextColor(Color.RED)

            }/*
            else{
                if(checkPassword(passwordString)==false) {
                    password.hint = "Lozinka mora imati najmanje 6 karaktera"
                    password.setHintTextColor(Color.RED)
                }
            }
*/
            if(!emailString.isEmpty() && !passwordString.isEmpty()&& checkPassword(passwordString)==true && checkEmail(emailString)==true) {

                var loginData= Login(emailString,passwordString)
                val authApi= RetrofitHelper.getInstanceNoAuth()
                val request=authApi.login(loginData)

                request.enqueue(object : retrofit2.Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if(response.isSuccessful()){
                            val token=response.body().toString()
                            Toast.makeText(
                                activity, token, Toast.LENGTH_LONG
                            ).show();
                            //TODO(navigate to main page)
                            SharedPreferencesHelper.addValue("jwt",token,activity!!)
                            val intent= Intent(activity!!, NavigationActivity::class.java)
                            startActivity(intent)
                        }else{
                            if(response.errorBody()!=null)
                                Toast.makeText(activity, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                        }


                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(
                            activity, t.toString(), Toast.LENGTH_LONG
                        ).show();
                    }
                })

            }
        }

        // zaboravljena lozinka
        forgottenPassword.setOnClickListener{
            val intent = Intent (getActivity(), ActivityForgottenPassword::class.java)
            getActivity()?.startActivity(intent)
        }





        return view

    }
    fun checkEmail(emailString:String):Boolean{
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        if(!(emailRegex.toRegex().matches(emailString))){
            Toast.makeText(
                activity, "Email adresa nije validna, pokušajte ponovo", Toast.LENGTH_LONG
            ).show();
            email.setHintTextColor(Color.RED)
            return false
        }
        else{
            return true
        }
    }

    fun checkPassword(passwordString:String):Boolean{
        if(passwordString.length<6){
            Toast.makeText(
                activity, "Uneta lozinka nije validna, pokušajte ponovo", Toast.LENGTH_LONG
            ).show();
            return false
        }
        return true
    }

}
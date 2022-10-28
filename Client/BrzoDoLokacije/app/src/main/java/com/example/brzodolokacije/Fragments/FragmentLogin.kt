package com.example.brzodolokacije.Fragments

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
import com.example.brzodolokacije.R


class FragmentLogin : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var forgottenPassword: TextView
    private lateinit var login: Button
    private lateinit var passwordString:String
    private lateinit var emailString:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
            }
            if(passwordString.isEmpty())
            {
                password.hint = "Unesite lozinku"
                password.setHintTextColor(Color.RED)
            }
            if(!emailString.isEmpty() && !passwordString.isEmpty()) {


                //proveri da li postoji u bazi

                //UPIT BAZI - ako postoji - idi na pocetnu stranu za logovanog


                //UPIT BAZI - ako ne postoji ili je pogresan unos - ispisi poruku

                //DODATI da li postoji korisnicko ime i da li je tacna lozinka

                Toast.makeText(
                    activity, "Korisnik sa unetim podacima nije pronađen. " + "\n" +
                            "Proverite podatke i pokušajte ponovo", Toast.LENGTH_LONG
                ).show();

            }
        }


        return view

    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragmentLogin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragmentLogin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

 */
}
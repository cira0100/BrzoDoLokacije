package com.example.brzodolokacije.Fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.R

class FragmentRegister : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var name: EditText
    private lateinit var register: Button
    private lateinit var usernameString:String
    private lateinit var nameString:String
    private lateinit var passwordString:String
    private lateinit var emailString:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View=inflater.inflate(R.layout.fragment_register, container, false)

        email = view.findViewById<View>(R.id.etFragmentRegisterEmail) as EditText
        password = view.findViewById<View>(R.id.etFragmentRegisterPassword) as EditText
        username = view.findViewById<View>(R.id.etFragmentRegisterUser) as EditText
        name = view.findViewById<View>(R.id.etFragmentRegisterName) as EditText
        register=view.findViewById<View>(R.id.btnFragmentRegisterRegister) as Button

        //osluskivanje unosa

        register.setOnClickListener{
            emailString=email.text.toString().trim()
            usernameString=username.text.toString().trim()
            nameString=name.text.toString().trim()
            passwordString=password.text.toString().trim()

            //prazan unos? neispravan email
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
            if(usernameString.isEmpty())
            {
                username.hint = "Unesite korisničko ime"
                username.setHintTextColor(Color.RED)
            }
            if(nameString.isEmpty())
            {
                name.hint = "Unesite ime i prezime"
                name.setHintTextColor(Color.RED)
            }
            if(!emailString.isEmpty() && !passwordString.isEmpty() && !nameString.isEmpty() && !usernameString.isEmpty()) {


                //proveri da li postoji u bazi

                //UPIT BAZI - ako postoji - greska korisnik je registrovan
                Toast.makeText(
                    activity, "Korisnik sa unetim podacima već postoji. " + "\n" +
                            "Da li želite da se prijavite?", Toast.LENGTH_LONG
                ).show();

                //UPIT BAZI - ako ne postoji dodaj u bazu

                //***DODATI broj karaktera lozinke, provera da li je email sa @ i .com

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
         * @return A new instance of fragment fragmentRegister.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragmentRegister().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}
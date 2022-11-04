package com.example.brzodolokacije.Fragments

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.Interfaces.IAuthApi
import com.example.brzodolokacije.Models.Auth.Register
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

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
            if(!emailString.isEmpty() && !passwordString.isEmpty() && !nameString.isEmpty()
                && !usernameString.isEmpty() && checkPassword(passwordString)==true
                && checkEmail(emailString)==true && checkUsername(usernameString)==true) {

                var registerData=Register(nameString,usernameString,emailString,passwordString)

                val authApi=RetrofitHelper.getInstance()

                val request=authApi.register(registerData)

                request.enqueue(object : retrofit2.Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if(response.isSuccessful()){
                            Toast.makeText(
                                activity, "Uspesna registracija. Verifikujte email.", Toast.LENGTH_LONG
                            ).show();
                            //TODO(navigate to login)
                        }else{
                            if(response.errorBody()!=null) {
                                Toast.makeText(
                                    activity,
                                    response.errorBody()!!.string(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }


                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Toast.makeText(
                            activity, "Greska, pokusajte ponovo.", Toast.LENGTH_LONG
                        ).show();
                    }
                })



                //***DODATI broj karaktera lozinke, provera da li je email sa @ i .com

            }
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
                activity, "Izaberite sigurniju lozinku", Toast.LENGTH_LONG
            ).show();
            return false
        }
        return true
    }

    fun checkUsername(usernameString:String):Boolean{
        if(usernameString.length<4){
            Toast.makeText(
                activity, "Izaberite korisničko ime sa 4 ili više karaktera ", Toast.LENGTH_LONG
            ).show();
            return false
        }
        return true
    }
}
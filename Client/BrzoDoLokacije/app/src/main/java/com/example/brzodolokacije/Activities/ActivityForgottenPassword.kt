package com.example.brzodolokacije.Activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.Models.Auth.JustMail
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ActivityForgottenPassword : AppCompatActivity() {
    private lateinit var sendCode: Button
    private lateinit var email: EditText
    private lateinit var emailString:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        sendCode=findViewById<View>(R.id.forgottenPasswordSendCode) as Button
        email=findViewById<View>(R.id.editTextTextPersonName) as EditText
        sendCode.setOnClickListener{
            emailString=email.text.toString().trim()

            if(!emailString.isEmpty() && checkEmail(emailString)==true) {

                var emailData= JustMail(emailString)
                val authApi= RetrofitHelper.getInstance()
                val request=authApi.forgotpass(emailData)
                val cont=this
                request.enqueue(object : retrofit2.Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        Log.d("main",response.code().toString())
                        Log.d("main",response.body().toString())
                        if(response.code()==200){
                            val intent = Intent(cont, ActivityForgottenPasswordVerify::class.java)
                            intent.putExtra("email", emailString)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@ActivityForgottenPassword,"Email ne postoji",Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Toast.makeText(this@ActivityForgottenPassword,"Email ne postoji",Toast.LENGTH_LONG).show()
                    }
                })
            }
            else{
                Toast.makeText(this@ActivityForgottenPassword,"Unesite validan email",Toast.LENGTH_LONG).show()
            }
        }

    }
    //from fragment login
    fun checkEmail(emailString:String):Boolean{
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        if(!(emailRegex.toRegex().matches(emailString))){
            Toast.makeText(
                this, "Email adresa nije validna, pokušajte ponovo", Toast.LENGTH_LONG
            ).show();
            email.setHintTextColor(Color.RED)
            return false
        }
        else{
            return true
        }
    }
}
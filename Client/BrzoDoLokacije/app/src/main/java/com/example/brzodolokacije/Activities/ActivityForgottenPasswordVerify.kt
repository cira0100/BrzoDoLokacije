package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.MainActivity
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.Models.Auth.ResetPass
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ActivityForgottenPasswordVerify : AppCompatActivity() {
    private lateinit var changePassword: Button
    private lateinit var pw:EditText
    private lateinit var pwchk:EditText
    private lateinit var kod:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password_verify)

        kod=findViewById<View>(R.id.editTextTextPersonName) as EditText
        pw=findViewById<View>(R.id.editTextoldPassword) as EditText
        pwchk =findViewById<View>(R.id.editTextTextPassword) as EditText
        changePassword=findViewById<View>(R.id.btnChangePassword) as Button
        changePassword.setOnClickListener{

            var email =intent.getStringExtra("email")
            var pwstr=pw.text.toString().trim()
            var pwchkstr=pwchk.text.toString().trim()
            var kodstr=kod.text.toString().trim()

            if(!kodstr.isEmpty() && checkPassword(pwstr,pwchkstr)){
                var resetData= ResetPass(email!!,kodstr,pwstr)
                val authApi= RetrofitHelper.getInstance()
                val request=authApi.resetpass(resetData)
                val cont=this
                request.enqueue(object : retrofit2.Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if(response.code()==200){
                            intent = Intent(cont, ActivityLoginRegister::class.java)
                            startActivity(intent)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    }
                })
            }
        }
    }

    //from fragment login
    fun checkPassword(passwordString:String,passwordConfirm:String):Boolean{

        if(passwordString.length<6){
            Toast.makeText(
                this, "Lozinke su prekratke", Toast.LENGTH_LONG
            ).show();
            return false
        }
        if(!passwordString.equals(passwordConfirm)){
            Toast.makeText(
                this, "Lozinke su se ne poklapaju", Toast.LENGTH_LONG
            ).show();
            return false
        }
        return true
    }
}
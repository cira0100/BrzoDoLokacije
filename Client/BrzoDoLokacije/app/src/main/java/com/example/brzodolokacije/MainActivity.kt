package com.example.brzodolokacije

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Activities.ActivityLoginRegister
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.chat.SignalRListener
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent:Intent
        SignalRListener.getInstance(this@MainActivity)
        if(checkLoggedIn()) {
            intent = Intent(this, NavigationActivity::class.java)
        }
        else
            intent= Intent(this, ActivityLoginRegister::class.java)


        startActivity(intent)
        finish()
    }

    fun checkLoggedIn():Boolean{
        var jwtString=SharedPreferencesHelper.getValue("jwt",this)
        if(jwtString==null)
            return false
        var jwt:JWT=JWT(jwtString)
        if(jwt.isExpired(30))
            return false
        refreshJwt(jwtString)
        return true




    }

    fun refreshJwt(token:String){
        if(token==null)
            return
        var refreshJwt= RetrofitHelper.getInstance().refreshJwt("Bearer "+token)
        refreshJwt.enqueue(object : retrofit2.Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful()){
                    val newToken=response.body().toString()
                    Toast.makeText(
                        applicationContext, token, Toast.LENGTH_LONG
                    ).show();
                    SharedPreferencesHelper.addValue("jwt",newToken,this@MainActivity)
                }else{
                    if(response.errorBody()!=null)
                        Toast.makeText(applicationContext, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                }


            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(
                    applicationContext, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })


    }
}
package com.example.brzodolokacije.Activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChangeUserData : AppCompatActivity() {
    private lateinit var username:EditText
    private lateinit var name:EditText
    private lateinit var editName:ImageView
    private lateinit var editUsername:ImageView
    private lateinit var confirmName:ImageView
    private lateinit var confirmUsername:ImageView
    private lateinit var errorName:TextView
    private lateinit var errorUsername:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_data)

        username=findViewById(R.id.tvActivityChangeUserDataUsername)
        name=findViewById(R.id.tvActivityChangeUserDataName)
        editName=findViewById(R.id.btnActivityChangeUserDataName)
        editUsername=findViewById(R.id.btnActivityChangeUserDataUsername)
        confirmName=findViewById(R.id.btnActivityChangeUserDataNameConfirm)
        confirmUsername=findViewById(R.id.btnActivityChangeUserDataUsernameConfirm)
        errorName=findViewById(R.id.btnActivityChangeUserDataNameError)
        errorUsername=findViewById(R.id.btnActivityChangeUserDataUsernameError)

        editUsername.isClickable=true
        editUsername.isVisible=true
        editUsername.isEnabled=true
        editUsername.isGone=false
        confirmUsername.isClickable=false
        confirmUsername.isVisible=false
        confirmUsername.isEnabled=false
        confirmUsername.isGone=true

        getUser()

        editUsername.setOnClickListener{
            username.setText("")
            editUsername.isClickable=false
            editUsername.isVisible=false
            editUsername.isEnabled=false
            editUsername.isGone=true
            confirmUsername.isClickable=true
            confirmUsername.isVisible=true
            confirmUsername.isEnabled=true
            confirmUsername.isGone=false

            //dodati on change  listener
            confirmUsername.setOnClickListener {
                val api = RetrofitHelper.getInstance()
                val token = SharedPreferencesHelper.getValue("jwt", this@ActivityChangeUserData)
                var data = api.changeMyUsername("Bearer " + token,username.toString().trim());
                data.enqueue(object : Callback<Int> {
                    override fun onResponse(
                        call: Call<Int>,
                        response: Response<Int>
                    ) {
                        var res=response.body()!!
                        Log.d("res",res.toString())
                        if(res==-1){
                            errorUsername.setText("Izaberite drugo korisničko ime")
                            errorUsername.setTextColor(Color.RED)
                        }
                        else if(res==-2){
                            errorUsername.setText("Nije moguće promeniti korisničko ime")
                            errorUsername.setTextColor(Color.RED)
                        }
                        else if(res==1){
                            errorUsername.setText("Korisničko ime je promenjeno")
                            errorUsername.setTextColor(Color.GREEN)
                            confirmUsername.isClickable=false
                            confirmUsername.isVisible=false
                            editUsername.isClickable=true
                            editUsername.isVisible=true
                        }
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        Log.d("changeUsername","failllllllllllllllllllllll")

                    }
                })

            }

        }


    }

    fun getUser(){
        val api = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", this@ActivityChangeUserData)
        var data = api.selfProfile("Bearer " + token);
        data.enqueue(object : Callback<UserReceive> {
            override fun onResponse(
                call: Call<UserReceive>,
                response: Response<UserReceive>
            ) {
                var user=response.body()!!
                username.setText(user.username)
                name.setText(user.name)
            }

            override fun onFailure(call: Call<UserReceive>, t: Throwable) {}
        })

    }
}
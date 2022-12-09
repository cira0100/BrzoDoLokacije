package com.example.brzodolokacije.Activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.brzodolokacije.R
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.exam.DBHelper.Companion.activity
import com.example.brzodolokacije.Models.Auth.ChangePass
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_my_profile_info.*
import retrofit2.Call
import retrofit2.Response


class ActivityChangePassword : AppCompatActivity() {

    private lateinit var oldPass:EditText
    private lateinit var oldPassError:TextView
    private lateinit var newPass:EditText
    private lateinit var newPassError:TextView
    private lateinit var confirmPass:EditText
    private lateinit var confirmPassError:TextView
    private lateinit var forgotten:TextView
    private lateinit var submit:Button
    private lateinit var backButton:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        oldPass=findViewById(R.id.tvActivityChangePasswordCurrentPass)
        oldPassError=findViewById(R.id.ActivityChangePasswordOldError)
        newPass=findViewById(R.id.tvActivityChangePasswordNewPass)
        newPassError=findViewById(R.id.btnActivityChangePasswordNewError)
        confirmPass=findViewById(R.id.ActivityChangePasswordNewPasswordConfirm)
        confirmPassError=findViewById(R.id.btnActivityChangePasswordConfirmError)
        forgotten=findViewById(R.id.btnActivityChangePasswordForgottenPass)
        submit=findViewById(R.id.ActivityChangePasswordChangePassword)
        backButton=findViewById(R.id.btnBackToUser)

        oldPassError.isVisible=false
        newPassError.isVisible=false
        confirmPassError.isVisible=false
        backButton.setOnClickListener{
            finish()
        }

        submit.setOnClickListener{
            oldPassError.isVisible=false
            newPassError.isVisible=false
            confirmPassError.isVisible=false

            if(oldPass.text.toString().trim().isNotEmpty()&&newPass.text.toString().trim().isNotEmpty()
                &&confirmPass.text.toString().trim().isNotEmpty()){

                if(newPass.text.toString().trim() == confirmPass.text.toString().trim()){

                //PROVERI DA LI JE TRENUTA LOZINKA ISTA KAO TRENUTNI UNOS
                    var cp= ChangePass(oldPass.text.toString(),newPass.text.toString())
                    val authApi= RetrofitHelper.getInstance()
                    val token= SharedPreferencesHelper.getValue("jwt",this@ActivityChangePassword)
                    val request=authApi.changePass("Bearer "+token,cp)

                    request.enqueue(object : retrofit2.Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if(response.isSuccessful()){
                                var done=response.body()!!
                                if(done==1){
                                    Toast.makeText(this@ActivityChangePassword, "Lozinka je uspešno promenjena.", Toast.LENGTH_LONG).show();
                                }
                                else if(done==-1){
                                    oldPassError.isVisible=true
                                    oldPassError.text="Uneta lozinka nije odgovarajuća, pokušajte ponovo."
                                    oldPassError.setTextColor(Color.RED)

                                }
                                else if(done==-2){
                                    Toast.makeText(this@ActivityChangePassword, "Lozinka nije promenjena. Pokušajte ponovo", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                if(response.errorBody()!=null)
                                    Toast.makeText(this@ActivityChangePassword, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                            }
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Toast.makeText(
                                activity, t.toString(), Toast.LENGTH_LONG
                            ).show();
                        }
                    })
                }
                else{
                    confirmPassError.isVisible=true
                    confirmPassError.text="Lozinke se ne podudaraju."
                    confirmPassError.setTextColor(Color.RED)
                }
            }
        }




    }
}
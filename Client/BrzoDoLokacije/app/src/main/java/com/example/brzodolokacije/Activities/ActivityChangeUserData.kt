package com.example.brzodolokacije.Activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.exam.DBHelper.Companion.activity
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ActivityChangeUserData : AppCompatActivity() {
    private lateinit var username:EditText
    private lateinit var name:EditText
    private lateinit var editName:ImageView
    private lateinit var editUsername:ImageView
    private lateinit var confirmName:ImageView
    private lateinit var confirmUsername:ImageView
    private lateinit var errorName:TextView
    private lateinit var errorUsername:TextView
    private lateinit var back:ImageView
    private lateinit var profilePicture:ImageView
    private lateinit var changeProfilePicture:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_data)

        //finish()
        username=findViewById(R.id.tvActivityChangeUserDataUsername)
        name=findViewById(R.id.tvActivityChangeUserDataName)
        editName=findViewById(R.id.btnActivityChangeUserDataName)
        editUsername=findViewById(R.id.btnActivityChangeUserDataUsername)
        confirmName=findViewById(R.id.btnActivityChangeUserDataNameConfirm)
        confirmUsername=findViewById(R.id.btnActivityChangeUserDataUsernameConfirm)
        errorName=findViewById(R.id.btnActivityChangeUserDataNameError)
        errorUsername=findViewById(R.id.btnActivityChangeUserDataUsernameError)
        back=findViewById(R.id.btnBackToUser)
        profilePicture=findViewById(R.id.tvActivityChangeUserDataProfilePicture)
        changeProfilePicture=findViewById(R.id.ChangeProfileEditImageEdit)

        editUsername.isClickable=true
        editUsername.isVisible=true
        editUsername.isEnabled=true
        editUsername.isGone=false
        confirmUsername.isClickable=false
        confirmUsername.isVisible=false
        confirmUsername.isEnabled=false
        confirmUsername.isGone=true
        errorUsername.isVisible=false

        editName.isClickable=true
        editName.isVisible=true
        editName.isEnabled=true
        editName.isGone=false
        confirmName.isClickable=false
        confirmName.isVisible=false
        confirmName.isEnabled=false
        confirmName.isGone=true
        errorName.isVisible=false
        getUser()

        editUsername.setOnClickListener{
            changeUsername()
            getUser()


        }

        changeProfilePicture.setOnClickListener {
            addProfilePicture()
        }

        editName.setOnClickListener{
            changeName()

        }
        back.setOnClickListener {
            finish()
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
                if(user.pfp!=null) {
                    Glide.with(this@ActivityChangeUserData)
                        .load(RetrofitHelper.baseUrl + "/api/post/image/" + user.pfp!!._id)
                        .circleCrop()//Round image
                        .into(profilePicture)
                }
            }

            override fun onFailure(call: Call<UserReceive>, t: Throwable) {}
        })

    }

    fun changeUsername(){
        if(username.text==null || username.text.trim().toString()=="")
        {
            Toast.makeText(this@ActivityChangeUserData,"Unesite korisničko ime",Toast.LENGTH_SHORT).show()
            return
        }
        val api = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", this@ActivityChangeUserData)
        var data = api.changeMyUsername("Bearer " + token,username.text.trim().toString());
        data.enqueue(object : Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>
            ) {
                var res=response.body()!!
                Log.d("res",res.toString())
                if(res==-1){
                    errorUsername.isVisible=true
                    errorUsername.isVisible=true
                    errorUsername.setText("Izaberite drugo korisničko ime")
                    errorUsername.setTextColor(Color.RED)
                }
                else if(res==-2){
                    errorUsername.isVisible=true
                    errorUsername.isVisible=true
                    errorUsername.setText("Nije moguće promeniti korisničko ime")
                    errorUsername.setTextColor(Color.RED)
                }
                else if(res==1){
                    errorUsername.isVisible=true
                    errorUsername.isVisible=true
                    errorUsername.setText("Korisničko ime je promenjeno")
                    errorUsername.setTextColor(Color.GREEN)
                    confirmUsername.isClickable=false
                    confirmUsername.isVisible=false
                    confirmUsername.isEnabled=false
                    confirmUsername.isGone=true
                    editUsername.isClickable=true
                    editUsername.isVisible=true
                    editUsername.isEnabled=true
                    editUsername.isGone=false
                    getUser()
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("changeUsername","failllllllllllllllllllllll")

            }
        })
    }

    fun changeName(){
        if(name.text==null || name.text.trim().toString()=="")
        {
            Toast.makeText(this@ActivityChangeUserData,"Unesite ime",Toast.LENGTH_SHORT).show()
            return
        }
        val api = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", this@ActivityChangeUserData)
        var data = api.changeMyName("Bearer " + token,name.text.trim().toString());
        data.enqueue(object : Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                var res=response.body()!!
                Log.d("res",res.toString())
                if(res==false){
                    errorName.isVisible=true
                    errorName.isVisible=true
                    errorName.setText("Nije moguće promeniti ime")
                    errorName.setTextColor(Color.RED)
                }
                else if(res){
                    errorName.isVisible=true
                    errorName.isVisible=true
                    errorName.setText("Ime je promenjeno")
                    errorName.setTextColor(Color.GREEN)
                    confirmName.isClickable=false
                    confirmName.isVisible=false
                    confirmName.isEnabled=false
                    confirmName.isGone=true
                    editName.isClickable=true
                    editName.isVisible=true
                    editName.isEnabled=true
                    editName.isGone=false
                    getUser()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d("changename","faillllllllll")

            }
        })
    }
    private fun addProfilePicture(){
        val intent= Intent(Intent.ACTION_PICK)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type="image/*"
        startActivityForResult(Intent.createChooser(intent,"Izaberi profilnu sliku"),201)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //nakon otvaranja
        if(requestCode==201 && resultCode== AppCompatActivity.RESULT_OK) {
            var imageUri = data!!.data

            val api =RetrofitHelper.getInstance()
            var inputStream=(imageUri?.let {
                this@ActivityChangeUserData.getContentResolver().openInputStream(
                    it
                )
            })
            val file: File = File.createTempFile("temp","pfp")
            file!!.writeBytes(inputStream!!.readBytes())
            var imageReq= RequestBody.create("image/*".toMediaTypeOrNull(),file)
            val imageBody: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, imageReq)
            val token= SharedPreferencesHelper.getValue("jwt", this@ActivityChangeUserData)
            val req=api.setPfp("Bearer "+token,imageBody)

            req.enqueue(object : retrofit2.Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if(response.isSuccessful()){
                        getUser()
                    }
                }
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {

                }
            })
        }
    }
}
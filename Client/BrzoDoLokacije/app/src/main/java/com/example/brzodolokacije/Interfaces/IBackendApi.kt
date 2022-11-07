package com.example.brzodolokacije.Interfaces

import com.example.brzodolokacije.Models.Auth.JustMail
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.Models.Auth.Register
import com.example.brzodolokacije.Models.Auth.ResetPass
import com.example.brzodolokacije.Models.PostPreview
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IBackendApi {
    @POST("/api/auth/login")
    fun login(@Body obj:Login): Call<String>
    @POST("/api/auth/register")
    fun register(@Body obj:Register):Call<ResponseBody>
    @POST("/api/auth/refreshJwt")
    fun refreshJwt(@Header("Authorization") authHeader:String): Call<String>
    @POST("/api/auth/forgotpass")
    fun forgotpass(@Body obj:JustMail):Call<ResponseBody>
    @POST("/api/auth/resetpass")
    fun resetpass(@Body obj:ResetPass):Call<ResponseBody>
    @GET("/api/post")
    fun getPosts(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>
    @Streaming
    @GET("/api/post/image/{id}")
    fun getImage(@Header("Authorization") authHeader:String,@Path("id") obj:String):Call<ResponseBody>
    //@POST("putanja")
    //fun add(@Body obj:Post,@Header("Authorization") authHeader:String):Call<Post>
}
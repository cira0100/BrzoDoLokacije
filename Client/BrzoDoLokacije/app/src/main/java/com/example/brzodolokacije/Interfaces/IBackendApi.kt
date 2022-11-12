package com.example.brzodolokacije.Interfaces

import com.example.brzodolokacije.Models.Auth.JustMail
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.Models.Auth.Register
import com.example.brzodolokacije.Models.Auth.ResetPass
import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.Rating
import com.example.brzodolokacije.Models.RatingReceive
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
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
    @POST("/api/Location/add")
    fun addLocation(@Header("Authorization") authHeader:String,@Body obj: Location ):Call<Location>
    @Multipart
    @POST("/api/Post/add")
    fun addPost(@Header("Authorization") authHeader:String, @Part images: Array<MultipartBody.Part?>?
                                                            ,@Part("_id") _id:RequestBody
                                                            ,@Part("description") description:RequestBody
                                                            ,@Part("locationId") locationId:RequestBody
                                                            ):Call<PostPreview>
    @POST("api/Post/posts/{id}/addrating")
    fun addRating(@Header("Authorization") authHeader:String,@Path("id") id:String,@Body rating: RatingReceive):Call<ResponseBody>
    //@POST("putanja")
    //fun add(@Body obj:Post,@Header("Authorization") authHeader:String):Call<Post>
}
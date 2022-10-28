package com.example.brzodolokacije.Interfaces

import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.Models.Auth.Register
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApi {
    @POST("/api/auth/login")
    fun login(@Body obj:Login): Call<String>
    @POST("/api/auth/register")
    fun register(@Body obj:Register):Call<ResponseBody>
}
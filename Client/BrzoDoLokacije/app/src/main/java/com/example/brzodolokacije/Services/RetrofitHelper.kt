package com.example.brzodolokacije.Services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val baseUrl="http://10.0.2.2:5279"
    fun getInstance():Retrofit{
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
//Usage
//Api = RetrofitHelper.getInstance().create(class)
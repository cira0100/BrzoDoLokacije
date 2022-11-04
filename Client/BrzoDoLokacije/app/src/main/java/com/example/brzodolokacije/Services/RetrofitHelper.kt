package com.example.brzodolokacije.Services

import com.example.brzodolokacije.Interfaces.IAuthApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    val baseUrl="http://10.0.2.2:5279"

    private var retrofit_noauth: IAuthApi? = null


    fun getInstanceNoAuth():IAuthApi{
        if(retrofit_noauth==null)
            retrofit_noauth= createInstanceNoAuth()
        return retrofit_noauth as IAuthApi
    }
    private fun createInstanceNoAuth():IAuthApi{
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(IAuthApi::class.java)

    }
}
//Usage
//Api = RetrofitHelper.getInstance().create(class)
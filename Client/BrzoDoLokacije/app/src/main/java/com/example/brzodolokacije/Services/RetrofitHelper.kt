package com.example.brzodolokacije.Services

import com.example.brzodolokacije.Interfaces.IBackendApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    //val baseUrl="http://10.0.2.2:5279"
    val baseUrl="http://147.91.204.115:10082"
    private var retrofit_noauth: IBackendApi? = null
    private var retrofit_auth: IBackendApi? = null

    fun getInstance():IBackendApi{
        if(retrofit_noauth==null)
            retrofit_noauth= createInstance()
        return retrofit_noauth as IBackendApi
    }
    private fun createInstance():IBackendApi{
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(IBackendApi::class.java)

    }
}
//Usage
//Api = RetrofitHelper.getInstance().create(class)
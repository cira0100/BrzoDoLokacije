package com.example.brzodolokacije.Services

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.auth0.android.jwt.JWT

object authCheck {



    fun isLoggedIn(act:Activity):Boolean{
        var jwtString=SharedPreferencesHelper.getValue("jwt",act)
        if(jwtString==null)
            return false
        var jwt: JWT = JWT(jwtString)
        if(jwt.isExpired(30))
            return false
        return true

    }
    fun isLoggedIn(act:FragmentActivity):Boolean{
        var jwtString=SharedPreferencesHelper.getValue("jwt",act)
        if(jwtString==null)
            return false
        var jwt: JWT = JWT(jwtString)
        if(jwt.isExpired(30))
            return false
        return true

    }
}
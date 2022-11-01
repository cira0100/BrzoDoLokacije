package com.example.brzodolokacije.Services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.fragment.app.FragmentActivity

object SharedPreferencesHelper {
    val prefName:String="OdysseyPreferences"

    fun getValue(key:String,act: FragmentActivity):String?{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return pref.getString(key,null)
    }
    fun getValue(key:String,act: Activity):String?{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return pref.getString(key,null)
    }
    fun addValue(key:String,value:String,act:FragmentActivity):Boolean{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        var editor:Editor=pref.edit()
        editor.putString(key,value)
        return editor.commit()
    }
    fun addValue(key:String,value:String,act:Activity):Boolean{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        var editor:Editor=pref.edit()
        editor.putString(key,value)
        return editor.commit()
    }
    fun removeValue(key:String,act: FragmentActivity):Boolean{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return pref.edit().remove(key).commit()
    }
    fun removeValue(key:String,act: Activity):Boolean{
        var pref:SharedPreferences=act.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return pref.edit().remove(key).commit()
    }


}
package com.example.brzodolokacije.Models

import android.os.Parcelable
import com.example.brzodolokacije.Models.LocationType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (
        var _id:String,
        var name:String,
        var city:String,
        var country:String,
        var adress:String,
        var latitude:Double,
        var longitude:Double,
        var type:LocationType
        ): Parcelable

package com.example.brzodolokacije.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostImage (
    var _id:String,
    var path:String
        ): Parcelable
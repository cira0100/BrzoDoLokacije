package com.example.brzodolokacije.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RatingData(
    var ratings:Double,
    var ratingscount:Int,
    var myrating:Int
):Parcelable
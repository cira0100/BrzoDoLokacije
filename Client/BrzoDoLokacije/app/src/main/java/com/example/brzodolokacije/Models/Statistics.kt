package com.example.brzodolokacije.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
data class Statistics(
    var totalViews:Int,
    var numberOfPosts:Int,
    var numberOfRatingsOnPosts:Int,
    var averagePostRatingOnPosts:Double,
    var monthlyViews:List<MonthlyViews>,
    var numberOfFavouritePosts:Int
)
@Parcelize
data class MonthlyViews(
    var month:Int,
    var views:Int
):Parcelable


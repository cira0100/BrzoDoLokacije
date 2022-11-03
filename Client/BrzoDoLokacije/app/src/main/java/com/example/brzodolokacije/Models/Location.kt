package com.example.brzodolokacije.Models

import com.example.brzodolokacije.Models.LocationType

data class Location (
        var _id:String,
        var name:String,
        var city:String,
        var country:String,
        var adress:String,
        var latitude:Double,
        var longitude:Double,
        var type:LocationType
        )

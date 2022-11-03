package com.example.brzodolokacije.Models

data class Location(
    val _id:String,
    val name:String,
    val city:String,
    val country:String,
    val address:String,
    val latitude:String,
    val longitude:String,
    val type:LocationTypes
)

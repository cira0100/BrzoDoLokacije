package com.example.brzodolokacije.Models

import java.util.*

data class Post (
    var _id:String,
    var creationDate: Date,
    var country:String,//drzava
    var city:String,
    var location:String,//naziv grada/naziv planine/naziv jezera/.......
    var type:LocationTypes //tip lokacije
    )
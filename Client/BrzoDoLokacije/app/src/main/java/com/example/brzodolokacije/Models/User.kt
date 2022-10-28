package com.example.brzodolokacije.Models

import java.util.Date

data class User (
    var _id:String,
    var name:String,
    var username:String,
    var email:String,
    var emailToken:String,
    var verified:Boolean,
    var password:String,
    var creationDate:Date

)
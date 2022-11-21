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
    var creationDate:Date,

    //profil
    var followers:List<User>,
    var followersCount:Int,
    var following:List<User>,
    var followingCount:Int,
    var postIds:List<Int>,
    var postNumber:Int

)
data class UserReceive(
    var _id:String,
    var name:String,
    var username:String,
    var email:String,
    var creationDate: Date,
    var pfp:PostImage?,
    var postcount:Int,
    var followers:List<User>,
    var followersNumber:Int,
    var following:List<User>,
    var followingNumber:Int,
    var postIds:List<Int>,
    var postNumber:Int
)
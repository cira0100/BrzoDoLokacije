package com.example.brzodolokacije.Models

import java.io.File
import java.util.*

data class Post (
    var _id:String,
    var ownerId:String,
    var location:Location,
    var description:String,
    var ratings:Number,
    var views:Int,
    var reviews:List<String>,
    var images:List<File>
    )
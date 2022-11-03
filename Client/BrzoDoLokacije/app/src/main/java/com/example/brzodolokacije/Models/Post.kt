package com.example.brzodolokacije.Models

import java.time.LocalDateTime
import java.util.*

data class Post (
    var _id:String,
    var location:Location,
    var ownerId:String,
    var description:String,
    var views:List<String>,
    var reports:List<String>,
    var ratings:List<Rating>,
    var comments:List<Comment>,
    var images:List<File>


    )

data class Comment (
    var userId:String,
    var comment:String,
    var parent:Comment,
    var timeStamp: LocalDateTime
)

data class Rating(
    var useridval :String,
    var rating:Int
)
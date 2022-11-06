package com.example.brzodolokacije.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import okhttp3.MultipartBody
import java.time.LocalDateTime

data class Post (
    var _id:String,
    var location:Location,
    var ownerId:String,
    var description:String,
    var views:List<String>,
    var reports:List<String>,
    var ratings:List<Rating>,
    var comments:List<Comment>,
    var images:List<PostImage>


    )
data class PostSend(
    var _id:String,
    var locationId:String,
    var description:String,
    var images: List<MultipartBody.Part>

)
@Parcelize
data class PostPreview (
    var _id:String,
    var ownerId:String,
    var location:Location,
    var description:String,
    var views:Int,
    var ratings:Float,
    var comments:List<Comment>,
    var images:List<PostImage>
):Parcelable


@Parcelize
data class Comment (
    var userId:String,
    var comment:String,
    var parent:Comment,
    var timeStamp: LocalDateTime
):Parcelable

data class Rating(
    var useridval :String,
    var rating:Int
)
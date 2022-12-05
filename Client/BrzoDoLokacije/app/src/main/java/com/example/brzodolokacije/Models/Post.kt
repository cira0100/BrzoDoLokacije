package com.example.brzodolokacije.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import okhttp3.MultipartBody
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
    var comments:List<CommentSend>?,
    var images:List<PostImage>,
    var ratingscount:Int,
    var createdAt:Date,
    var lastViewed: Date?, //samo za istoriju pregleda
    var tags:List<String>?,
    var favourites:List<String>?


    //nedostaju datum i vreme kreiranja
):Parcelable


@Parcelize
data class Comment (
    var userId:String,
    var comment:String,
    var parent:Comment,
    var timeStamp: LocalDateTime
):Parcelable

@Parcelize
data class CommentSend(
    var _id: String,
    var userId: String,
    var comment: String,
    var parentId: String,
    //var timestamp:LocalDateTime,
    var username: String,
    var replies: List<CommentSend>?
):Parcelable

data class CommentReceive(
    var comment: String,
    var parentId: String
)

data class Rating(
    var useridval :String,
    var rating:Int
)

data class RatingReceive(
    var rating:Int,
    var postId:String
)

data class PagedPosts(
    var page:Int,
    var index:Int,
    var totalpages:Int,
    var totalposts:Int,
    var posts:MutableList<PostPreview>
)

data class SearchParams(
    var locationId: String,
    var sorttype:Int,
    var filterdate:Int
)
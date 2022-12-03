package com.example.brzodolokacije.Interfaces

import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.Models.Auth.JustMail
import com.example.brzodolokacije.Models.Auth.Login
import com.example.brzodolokacije.Models.Auth.Register
import com.example.brzodolokacije.Models.Auth.ResetPass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface IBackendApi {
    @POST("/api/auth/login")
    fun login(@Body obj:Login): Call<String>
    @POST("/api/auth/register")
    fun register(@Body obj:Register):Call<ResponseBody>
    @POST("/api/auth/refreshJwt")
    fun refreshJwt(@Header("Authorization") authHeader:String): Call<String>
    @POST("/api/auth/forgotpass")
    fun forgotpass(@Body obj:JustMail):Call<ResponseBody>
    @POST("/api/auth/resetpass")
    fun resetpass(@Body obj:ResetPass):Call<ResponseBody>
    @GET("/api/post")
    fun getPosts(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>
    @GET("/api/Post/posts/{id}")
    fun addView(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<PostPreview>
    @POST("/api/Location/add")
    fun addLocation(@Header("Authorization") authHeader:String,@Body obj: Location ):Call<Location>
    @Multipart
    @POST("/api/Post/add")
    fun addPost(@Header("Authorization") authHeader:String, @Part images: Array<MultipartBody.Part?>?
                                                            ,@Part("_id") _id:RequestBody
                                                            ,@Part("description") description:RequestBody
                                                            ,@Part("locationId") locationId:RequestBody
                                                            ,@Part("tags") tags:RequestBody
                                                            ):Call<PostPreview>
    @POST("api/Post/posts/{id}/addrating")
    fun addRating(@Header("Authorization") authHeader:String,@Path("id") id:String,@Body rating: RatingReceive):Call<RatingData>
    @POST("api/Post/posts/{id}/addcomment")
    fun addComment(@Header("Authorization") authHeader:String,@Path("id") id:String,@Body rating: CommentReceive):Call<CommentSend>
    @GET("api/Post/posts/{id}/listcomments")
    fun getComments(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<MutableList<CommentSend>>


    @Multipart
    @POST("/api/user/profile/pfp")
    fun setPfp(@Header("Authorization") authHeader:String, @Part image: MultipartBody.Part):Call<ResponseBody>
    @GET("/api/user/profile")
    fun selfProfile(@Header("Authorization") authHeader:String):Call<UserReceive>
    @GET("/api/user/{username}/profile")
    fun getProfile(@Header("Authorization") authHeader:String,@Path("username") username:String):Call<UserReceive>

    @GET("/api/user/posts")
    fun getMyPosts(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>

    @GET("/api/post/locations/{id}/posts")
    suspend fun getPagedPosts(@Header("Authorization") authHeader: String,
                        @Path("id") locationId:String,
                        @Query("page") page:Int,
                        @Query("sorttype") sorttype:Int,
                        @Query("filterdate") filterdate:Int
                        ):PagedPosts
    @POST("/api/message/add")
    fun sendMessage(@Header("Authorization") authHeader:String,@Body message:MessageSend):Call<Message>
    @GET("/api/message/myMessages")
    fun getNewMessages(@Header("Authorization") authHeader:String):Call<MutableList<MessageReceive>?>
    @GET("/api/user/history")
    fun getMyHistory(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>
//@POST("putanja")
    //fun add(@Body obj:Post,@Header("Authorization") authHeader:String):Call<Post>

    @GET("/api/user/{id}/followers")
    fun getFollowers(@Header("Authorization") authHeader:String,@Path("id") id:String):Call <MutableList<UserReceive>>

    @GET("/api/user/{id}/following")
    fun getFollowing(@Header("Authorization") authHeader:String,@Path("id") id:String):Call <MutableList<UserReceive>>

    @GET("/api/user/{id}/addFollower")
    fun addFollower(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<Boolean>
    @GET("/api/user/{id}/id/profile")
    fun getProfileFromId(@Header("Authorization") authHeader:String,@Path("id") username:String):Call<UserReceive>

    @GET("/api/Post/posts/get10MostViewed")
    fun get10MostViewed(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>

    @GET("/api/Post/posts/get10Best")
    fun get10Best(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>

    @GET("/api/Post/posts/get10Newest")
    fun get10Newest(@Header("Authorization") authHeader:String):Call<MutableList<PostPreview>>

    @GET("api/Location/search")
    fun searchLocationsQuery(@Header("Authorization") authHeader:String,@Query("query") query: String):Call<MutableList<Location>>

    @GET("/api/user/{id}/myFollowings")
    fun getMyFollowings(@Header("Authorization") authHeader:String):Call <MutableList<UserReceive>>

    @GET("/api/user/{id}/checkIfAlreadyFollow")
    fun checkIfAlreadyFollow(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<Boolean>

    @GET("/api/user/{id}/unfollow")
    fun unfollow(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<Boolean>

    @GET("api/Post/posts/{id}/getUserPosts")
    fun getUsersPosts(@Header("Authorization") authHeader:String,@Path("id") id:String):Call<MutableList<PostPreview>>

    @GET("/api/user/{id}/myFollowers")
    fun getMyFollowers(@Header("Authorization") authHeader:String):Call <MutableList<UserReceive>>
    @GET("/api/Post/favourite/{id}")
    fun addRemoveFavourite(@Header("Authorization") authHeader:String,@Path("id") id:String):Call <Boolean>

}
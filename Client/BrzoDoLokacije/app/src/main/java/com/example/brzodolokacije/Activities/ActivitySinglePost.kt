package com.example.brzodolokacije.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import okhttp3.ResponseBody
import okhttp3.internal.notifyAll
import retrofit2.Call
import retrofit2.Response


class ActivitySinglePost : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var layoutManagerImages: RecyclerView.LayoutManager? = null
    private var layoutManagerComments: RecyclerView.LayoutManager? = null
    private var adapterImages: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var adapterComments: RecyclerView.Adapter<CommentsAdapter.ViewHolder>? = null
    private var recyclerViewImages: RecyclerView?=null
    private var recyclerViewComments: RecyclerView?=null
    private lateinit var post:PostPreview
    private var comments:MutableList<CommentSend>?=mutableListOf()
    private var starNumber:Number=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        post= intent.extras?.getParcelable("selectedPost")!!

        //instantiate adapter and linearLayout
        adapterImages= PostImageAdapter(this@ActivitySinglePost, post.images as MutableList<PostImage>)
        layoutManagerImages= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewImages = binding.rvMain
        buildRecyclerViewComments()
        requestGetComments()

        // set recyclerView attributes
        recyclerViewImages?.setHasFixedSize(true)
        recyclerViewImages?.layoutManager = layoutManagerImages
        recyclerViewImages?.adapter = adapterImages
        loadTextComponents()
        setRatingListeners()
    }

    fun buildRecyclerViewComments(){
        recyclerViewComments=binding.rvComments
        adapterComments=CommentsAdapter(comments as MutableList<CommentSend>)
        layoutManagerComments= LinearLayoutManager(this@ActivitySinglePost,LinearLayoutManager.VERTICAL,false)
        recyclerViewComments!!.setHasFixedSize(true)
        recyclerViewComments!!.layoutManager=layoutManagerComments
        recyclerViewComments!!.adapter= adapterComments
    }

    fun setRatingListeners(){
        val emptyStar=R.drawable.empty_star
        val fullStar=R.drawable.full_star


        binding.rateStar1.setOnClickListener {
            Toast.makeText(this,"kliknuta prva zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(emptyStar)
            binding.rateStar3.setImageResource(emptyStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=1
        }
        binding.rateStar2.setOnClickListener {
            Toast.makeText(this,"kliknuta druga zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(emptyStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=2
        }
        binding.rateStar3.setOnClickListener {
            Toast.makeText(this,"kliknuta treca zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(fullStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=3
        }
        binding.rateStar4.setOnClickListener {
            Toast.makeText(this,"kliknuta cetvrta zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(fullStar)
            binding.rateStar4.setImageResource(fullStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=4
        }
        binding.rateStar5.setOnClickListener {
            Toast.makeText(this,"kliknuta peta zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(fullStar)
            binding.rateStar4.setImageResource(fullStar)
            binding.rateStar5.setImageResource(fullStar)
            starNumber=5
        }
        binding.submitRating.setOnClickListener{
            if(starNumber.toInt()>0){
                val rating= RatingReceive(starNumber.toInt(),post._id)
                requestAddRating(rating)
                Toast.makeText(this,"poslato",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPostComment.setOnClickListener {
            if(binding.NewComment.text.isNotEmpty()){
                val comment=CommentReceive(binding.NewComment.text.toString(),"")
                requestAddComment(comment)
            }
        }

    }

    fun requestAddComment(comment:CommentReceive){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivitySinglePost)
        val request=postApi.addComment("Bearer "+token,post._id,comment)
        request.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(response.isSuccessful){
                    requestGetComments()
                    Toast.makeText(
                        this@ActivitySinglePost, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()
                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.message().toString())
                }


            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }
    fun requestGetComments(){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivitySinglePost)
        val request=postApi.getComments("Bearer "+token,post._id)
        request.enqueue(object : retrofit2.Callback<MutableList<CommentSend>?> {
            override fun onResponse(call: Call<MutableList<CommentSend>?>, response: Response<MutableList<CommentSend>?>) {
                if(response.isSuccessful){
                    comments= response.body()!!
                    if(comments!=null && comments!!.isNotEmpty()){
                        buildRecyclerViewComments()
                        if(comments!=null)
                            binding.tvCommentCount.text=comments?.size.toString()
                        else
                            binding.tvCommentCount.text="0"
                    }
                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.message().toString())
                }


            }

            override fun onFailure(call: Call<MutableList<CommentSend>?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }

    fun requestAddRating(rating:RatingReceive){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivitySinglePost)
        val request=postApi.addRating("Bearer "+token,post._id,rating)
        request.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(response.isSuccessful){
                    Toast.makeText(
                        this@ActivitySinglePost, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()
                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.errorBody().toString())
                }


            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }

    private fun loadTextComponents() {
        binding.apply {
            tvTitle.text= post.location.name
            tvTitle.invalidate()
            tvLocationType.text="TODO"
            tvLocationType.invalidate()
            tvLocationParent.text="TODO"
            tvLocationParent.invalidate()
            tvRating.text=post.ratings.toString()
            tvRating.invalidate()
            tvNumberOfRatings.text=post.ratings.toString()
            tvNumberOfRatings.invalidate()
            tvDescription.text=post.description
            tvDescription.invalidate()
        }
    }

}

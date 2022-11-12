package com.example.brzodolokacije.Activities

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.Rating
import com.example.brzodolokacije.Models.RatingReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class ActivitySinglePost : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null
    private lateinit var post:PostPreview
    private var starNumber:Number=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        post= intent.extras?.getParcelable("selectedPost")!!
        //load data for the list

        //instantiate adapter and linearLayout
        adapterVar= PostImageAdapter(this@ActivitySinglePost, post.images as MutableList<PostImage>)
        layoutManagerVar= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerView = binding.rvMain
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        loadTextComponents()
        setRatingListeners()
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

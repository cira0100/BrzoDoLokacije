package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Response


class FragmentSinglePostDescription : Fragment() {

    private lateinit var descriptionContainer:TextView
    private lateinit var star1:ImageView
    private lateinit var star2:ImageView
    private lateinit var star3:ImageView
    private lateinit var star4:ImageView
    private lateinit var star5:ImageView
    private var starNumber:Number=0
    private lateinit var post:PostPreview

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view=inflater.inflate(R.layout.fragment_single_post_description, container, false)



        //uzmi post prosledjen iz single post
        var args = arguments
        var jsonPostObject = args!!.getString("post")
        post= Gson().fromJson(jsonPostObject, PostPreview::class.java)

        //setuj opis
        descriptionContainer=view.findViewById(R.id.tvDescription)
        descriptionContainer.text=post.description.toString()

        //setuj zvezdice
        star1=view.findViewById(R.id.rateStar1)
        star2=view.findViewById(R.id.rateStar2)
        star3=view.findViewById(R.id.rateStar3)
        star4=view.findViewById(R.id.rateStar4)
        star5=view.findViewById(R.id.rateStar5)

        setRatingListeners()
        val alreadyrated= RatingReceive(starNumber.toInt(),post._id)
        requestAddRating(alreadyrated)


        return view
    }
    fun setRatingListeners() {
        val emptyStar = R.drawable.ic_round_star_outline_24
        val fullStar = R.drawable.ic_baseline_star_rate_24

        star1.setOnClickListener {
            //Toast.makeText(this,"kliknuta prva zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(emptyStar)
            star3.setImageResource(emptyStar)
            star4.setImageResource(emptyStar)
            star5.setImageResource(emptyStar)
            starNumber=1
            rate(starNumber)
        }
        star1.setOnClickListener {
            //Toast.makeText(this,"kliknuta druga zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(emptyStar)
            star4.setImageResource(emptyStar)
            star5.setImageResource(emptyStar)
            starNumber=2
            rate(starNumber)
        }
        star1.setOnClickListener {
            //Toast.makeText(this,"kliknuta treca zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(fullStar)
            star4.setImageResource(emptyStar)
            star5.setImageResource(emptyStar)
            starNumber=3
            rate(starNumber)
        }
        star1.setOnClickListener {
            Toast.makeText(requireActivity(),"kliknuta cetvrta zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(fullStar)
            star4.setImageResource(fullStar)
            star5.setImageResource(emptyStar)
            starNumber=4
            rate(starNumber)
        }
        star1.setOnClickListener {
            //Toast.makeText(this,"kliknuta peta zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(fullStar)
            star4.setImageResource(fullStar)
            star5.setImageResource(fullStar)
            starNumber=5
            rate(starNumber)
        }


    }

    fun rate(starNumber:Number){
        if(starNumber.toInt()>0){
            val rating= RatingReceive(starNumber.toInt(),post._id)
            requestAddRating(rating)
        }
    }

    fun requestAddRating(rating:RatingReceive) {
        val postApi = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request = postApi.addRating("Bearer " + token, post._id, rating)
        request.enqueue(object : retrofit2.Callback<RatingData> {
            override fun onResponse(call: Call<RatingData>, response: Response<RatingData>) {
                if (response.isSuccessful) {
                    var data = response.body()!!
                    Log.d(
                        "--------------",
                        data.ratings.toString() + " " + data.ratingscount.toString()
                    )
                    when (data.myrating) {
                        1 -> star1.performClick()
                        2 -> star1.performClick()
                        3 -> star3.performClick()
                        4 -> star4.performClick()
                        5 -> star5.performClick()
                        else -> {
                            val emptyStar = R.drawable.empty_star
                            star1.setImageResource(emptyStar)
                            star2.setImageResource(emptyStar)
                            star3.setImageResource(emptyStar)
                            star4.setImageResource(emptyStar)
                            star5.setImageResource(emptyStar)
                        }
                    }
                    /*Toast.makeText(
                            this@ActivitySinglePost, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()*/
                } else {
                    if (response.errorBody() != null)
                        Log.d("main1", response.errorBody().toString())
                }


            }

            override fun onFailure(call: Call<RatingData>, t: Throwable) {
                Log.d("main2", t.message.toString())
            }
        })
    }
}
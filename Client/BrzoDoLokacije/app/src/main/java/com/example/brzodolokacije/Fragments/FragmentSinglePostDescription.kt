package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
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
    private lateinit var parentact:ActivitySinglePost
    private lateinit var ocenitext:TextView
    private lateinit var userid:String
    private lateinit var del:TextView
    private lateinit var delbtn:Button
    private lateinit var delbtnY:Button
    private lateinit var delbtnN:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view=inflater.inflate(R.layout.fragment_single_post_description, container, false)
        parentact = activity as ActivitySinglePost


        //uzmi post prosledjen iz single post
        var args = arguments
        var jsonPostObject = args!!.getString("post")
        post= Gson().fromJson(jsonPostObject, PostPreview::class.java)


        //setuj opis
        descriptionContainer=view.findViewById(R.id.tvDescription)
        descriptionContainer.text=post.description.toString()


        del=view.findViewById(R.id.tvObrisi)
        delbtn=view.findViewById(R.id.btnObrisi)
        delbtnY=view.findViewById(R.id.btnObrisiY)
        delbtnN=view.findViewById(R.id.btnObrisiN)
        userid=""
        ocenitext=view.findViewById(R.id.title)
        //setuj zvezdice
        star1=view.findViewById(R.id.rateStar1)
        star2=view.findViewById(R.id.rateStar2)
        star3=view.findViewById(R.id.rateStar3)
        star4=view.findViewById(R.id.rateStar4)
        star5=view.findViewById(R.id.rateStar5)

        setRatingListeners()

        val alreadyrated= RatingReceive(starNumber.toInt(),post._id)
        requestAddRating(alreadyrated)

        toggleStarRatings()
        return view
    }
    fun setRatingListeners() {
        val emptyStar = R.drawable.ic_round_star_outline_24
        val fullStar = R.drawable.ic_baseline_star_rate_24
        val offStar = R.drawable.ic_baseline_star_24

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
        star2.setOnClickListener {
            //Toast.makeText(this,"kliknuta druga zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(emptyStar)
            star4.setImageResource(emptyStar)
            star5.setImageResource(emptyStar)
            starNumber=2
            rate(starNumber)
        }
        star3.setOnClickListener {
            //Toast.makeText(this,"kliknuta treca zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(fullStar)
            star4.setImageResource(emptyStar)
            star5.setImageResource(emptyStar)
            starNumber=3
            rate(starNumber)
        }
        star4.setOnClickListener {
            //Toast.makeText(requireActivity(),"kliknuta cetvrta zvezdica",Toast.LENGTH_SHORT).show()
            star1.setImageResource(fullStar)
            star2.setImageResource(fullStar)
            star3.setImageResource(fullStar)
            star4.setImageResource(fullStar)
            star5.setImageResource(emptyStar)
            starNumber=4
            rate(starNumber)
        }
        star5.setOnClickListener {
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
                    parentact.updateratings(data.ratingscount,data.ratings)
                    if(rating.rating==0) {
                        val emptyStar = R.drawable.empty_star
                        val fullStar = R.drawable.ic_baseline_star_rate_24

                        when (data.myrating) {
                            1 -> {
                                star1.setImageResource(fullStar)
                                star2.setImageResource(emptyStar)
                                star3.setImageResource(emptyStar)
                                star4.setImageResource(emptyStar)
                                star5.setImageResource(emptyStar)
                            }
                            2 -> {
                                star1.setImageResource(fullStar)
                                star2.setImageResource(fullStar)
                                star3.setImageResource(emptyStar)
                                star4.setImageResource(emptyStar)
                                star5.setImageResource(emptyStar)
                            }
                            3 -> {
                                star1.setImageResource(fullStar)
                                star2.setImageResource(fullStar)
                                star3.setImageResource(fullStar)
                                star4.setImageResource(emptyStar)
                                star5.setImageResource(emptyStar)
                            }
                            4 -> {
                                star1.setImageResource(fullStar)
                                star2.setImageResource(fullStar)
                                star3.setImageResource(fullStar)
                                star4.setImageResource(fullStar)
                                star5.setImageResource(emptyStar)
                            }
                            5 -> {
                                star1.setImageResource(fullStar)
                                star2.setImageResource(fullStar)
                                star3.setImageResource(fullStar)
                                star4.setImageResource(fullStar)
                                star5.setImageResource(fullStar)
                            }
                            else -> {
                                star1.setImageResource(emptyStar)
                                star2.setImageResource(emptyStar)
                                star3.setImageResource(emptyStar)
                                star4.setImageResource(emptyStar)
                                star5.setImageResource(emptyStar)
                            }
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
    fun toggleStarRatings(){
        var token= SharedPreferencesHelper.getValue("jwt", requireActivity()).toString()
        val api= RetrofitHelper.getInstance()
        val request= api.getUserId("Bearer " + token)
        request.enqueue(object : retrofit2.Callback<String>  {
            override fun onResponse(call: Call<String>,
                                    response: Response<String>
            ) {
                if (response.body() == null) {
                    return
                }
                userid=response.body().toString()
                Log.d("UID TEST",userid)
                if(post.ownerId==userid){
                    //val offStar = R.drawable.ic_baseline_star_24
                    ocenitext.text="Vlasnik posta ne moze ocenjivati sebe"
                    /*star1.setImageResource(offStar)
                    star2.setImageResource(offStar)
                    star3.setImageResource(offStar)
                    star4.setImageResource(offStar)
                    star5.setImageResource(offStar)*/
                    star1.isEnabled=false
                    star2.isEnabled=false
                    star3.isEnabled=false
                    star4.isEnabled=false
                    star5.isEnabled=false

                    ocenitext.isVisible=false
                    ocenitext.isGone=true
                    star1.isVisible=false
                    star1.isGone=true
                    star2.isVisible=false
                    star2.isGone=true
                    star3.isVisible=false
                    star3.isGone=true
                    star4.isVisible=false
                    star4.isGone=true
                    star5.isVisible=false
                    star5.isGone=true

                    delbtn.isGone=false
                    delbtn.setOnClickListener{
                        del.isGone=false
                        delbtnY.isGone=false
                        delbtnN.isGone=false
                        delbtn.isGone=true
                        delbtnY.setOnClickListener {
                            deletePostreq()
                        }
                        delbtnN.setOnClickListener {
                            del.isGone=true
                            delbtnY.isGone=true
                            delbtnN.isGone=true
                            delbtn.isGone=false
                        }
                    }


                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })


    }

    fun deletePostreq(){
        var token= SharedPreferencesHelper.getValue("jwt", requireActivity()).toString()
        val api= RetrofitHelper.getInstance()
        val request= api.DeletePost("Bearer " + token,post._id)
        request.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful){
                    SharedPreferencesHelper.addValue("jwt",token,activity!!)
                    val intent= Intent(activity!!, NavigationActivity::class.java)
                    startActivity(intent)
                    activity!!.finish()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

}
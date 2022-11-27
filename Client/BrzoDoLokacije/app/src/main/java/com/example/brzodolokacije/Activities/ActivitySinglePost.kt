package com.example.brzodolokacije.Activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_single_post.*
import okhttp3.ResponseBody
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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
    private lateinit var userData:UserReceive
    private lateinit var user:TextView

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
        translateOwnerIdToName(post.ownerId)

        binding.tvUser.setOnClickListener {
            val intent: Intent = Intent(this@ActivitySinglePost,ActivityUserProfile::class.java)
            var b= Bundle()
            intent.putExtra("user", Gson().toJson(userData))
            this.startActivity(intent)
        }
        binding.tvLocationType.setOnClickListener{
            getMap()

        }
    }
    fun getMap(){
        val mapDialogue = BottomSheetDialog(this@ActivitySinglePost, android.R.style.Theme_Black_NoTitleBar)
        mapDialogue.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
        mapDialogue.setContentView(R.layout.map_dialogue)
        mapDialogue.setCancelable(true)
        mapDialogue.setCanceledOnTouchOutside(true)
        var map: MapView? = null
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map=mapDialogue.findViewById(R.id.MapDialogueMapView)
            //findViewById(R.id.MapDialogueMapView) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK);
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);
        val mapController = map!!.controller
        mapController.setZoom(15)

        val LocMarker = GeoPoint(post.location.latitude,post.location.longitude)
        val startMarker = Marker(map)
        val marker = ContextCompat.getDrawable(this@ActivitySinglePost, R.drawable.ic_baseline_location_on_24);
        startMarker.icon=marker
        startMarker.setPosition(LocMarker)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map!!.getOverlays().add(startMarker)
        map!!.controller.setCenter(LocMarker)
        mapDialogue.show()

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
            else{
                Toast.makeText(this@ActivitySinglePost,"Unesite tekst komentara.",Toast.LENGTH_LONG).show()
            }
        }
        addView()

    }

    fun requestAddComment(comment:CommentReceive){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivitySinglePost)
        val request=postApi.addComment("Bearer "+token,post._id,comment)
        request.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(response.isSuccessful){
                    requestGetComments()
                    binding.NewComment.text.clear()
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
                    //zasad hardkodovano, zameniti te vrednosti sa brojem ocena kada se doda
                    post.ratings=((post.ratings)*10+rating.rating)/11
                    binding.tvRating.text=String.format("%.2f",post.ratings)
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
            tvLocationType.text="TODO Click to open map"
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
    fun addView() {
        var token= SharedPreferencesHelper.getValue("jwt", this).toString()
        val Api= RetrofitHelper.getInstance()
        val request=Api.addView("Bearer "+token,post._id)
        request.enqueue(object : retrofit2.Callback<PostPreview?> {
            override fun onResponse(call: Call<PostPreview?>, response: Response<PostPreview?>) {

            }

            override fun onFailure(call: Call<PostPreview?>, t: Throwable) {

            }
        })
    }

    fun translateOwnerIdToName(id:String) {
        var token= SharedPreferencesHelper.getValue("jwt", this).toString()
        val api= RetrofitHelper.getInstance()
        val request= api.getProfileFromId("Bearer " + token, id)
        request.enqueue(object : retrofit2.Callback<UserReceive>  {
            override fun onResponse(call: Call<UserReceive>,
                                    response: Response<UserReceive>) {
                if (response.body() == null) {
                    return
                }
                userData = response.body()!!
                binding.tvUser.text= userData!!.username.toString()
            }

            override fun onFailure(call: Call<UserReceive>, t: Throwable) {

            }
        })
    }

}

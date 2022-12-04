package com.example.brzodolokacije.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
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
    private var favouriteImage:ImageView?=null
    public  lateinit var post:PostPreview
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
        favouriteImage=binding.ivFavourite
        // set recyclerView attributes
        recyclerViewImages?.setHasFixedSize(true)
        recyclerViewImages?.layoutManager = layoutManagerImages
        recyclerViewImages?.adapter = adapterImages
        loadTextComponents()
        setRatingListeners()
        translateOwnerIdToName(post.ownerId)
        loadFavourite()
        val alreadyrated= RatingReceive(starNumber.toInt(),post._id)
        requestAddRating(alreadyrated)

        binding.tvUser.setOnClickListener {
            val intent: Intent = Intent(this@ActivitySinglePost,ActivityUserProfile::class.java)
            var b= Bundle()
            intent.putExtra("user", Gson().toJson(userData))
            this.startActivity(intent)
        }
        binding.tvLocationType.setOnClickListener{
            getMap()

        }
        favouriteImage!!.setOnClickListener{
            addRemoveFavourite()
        }
    }
    fun loadFavourite(){
        if(post.favourites!=null){
            var jwtString=SharedPreferencesHelper.getValue("jwt",this)
            var jwt: JWT = JWT(jwtString!!)
            var userId=jwt.getClaim("id").asString()
            if(post.favourites!!.contains(userId))
            {
                favouriteImage!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))
            }else{
                favouriteImage!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24));

            }

        }
    }
    fun addRemoveFavourite(){
        var token= SharedPreferencesHelper.getValue("jwt", this).toString()
        val Api= RetrofitHelper.getInstance()
        val request=Api.addRemoveFavourite("Bearer "+token,post._id)
        request.enqueue(object : retrofit2.Callback<Boolean?> {
            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                if(response.isSuccessful && response.body() == true)
                    favouriteImage!!.setImageDrawable(ContextCompat.getDrawable(this@ActivitySinglePost, R.drawable.ic_baseline_favorite_24))
                else
                    favouriteImage!!.setImageDrawable(ContextCompat.getDrawable(this@ActivitySinglePost, R.drawable.ic_baseline_favorite_border_24));
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {

            }
        })

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
        adapterComments=CommentsAdapter(comments as MutableList<CommentSend>,this@ActivitySinglePost)
        layoutManagerComments= LinearLayoutManager(this@ActivitySinglePost,LinearLayoutManager.VERTICAL,false)
        recyclerViewComments!!.setHasFixedSize(false)
        recyclerViewComments!!.layoutManager=layoutManagerComments
        recyclerViewComments!!.adapter= adapterComments
    }
    fun hideKeyboard(item: EditText){
        var imm: InputMethodManager =this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(item.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun setRatingListeners() {
            val emptyStar = R.drawable.empty_star
            val fullStar = R.drawable.full_star
            /*var starlist: ArrayList<ImageButton> = arrayListOf()
            starlist.add(findViewById(R.id.rateStar1) as ImageButton)
            starlist.add(findViewById(R.id.rateStar2) as ImageButton)
            starlist.add(findViewById(R.id.rateStar3) as ImageButton)
            starlist.add(findViewById(R.id.rateStar4) as ImageButton)
            starlist.add(findViewById(R.id.rateStar5) as ImageButton)
            for (i in 0..4) {
                starlist[i].setOnClickListener {
                    for (j in 1..i) {
                        starlist[j].setImageResource(fullStar)
                    }
                    for (k in i..5) {
                        starlist[k].setImageResource(emptyStar)
                    }
                    starNumber = i+1;
                }
            }*/

        binding.rateStar1.setOnClickListener {
            //Toast.makeText(this,"kliknuta prva zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(emptyStar)
            binding.rateStar3.setImageResource(emptyStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=1
        }
        binding.rateStar2.setOnClickListener {
            //Toast.makeText(this,"kliknuta druga zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(emptyStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=2
        }
        binding.rateStar3.setOnClickListener {
            //Toast.makeText(this,"kliknuta treca zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(fullStar)
            binding.rateStar4.setImageResource(emptyStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=3
        }
        binding.rateStar4.setOnClickListener {
            //Toast.makeText(this,"kliknuta cetvrta zvezdica",Toast.LENGTH_SHORT).show()
            binding.rateStar1.setImageResource(fullStar)
            binding.rateStar2.setImageResource(fullStar)
            binding.rateStar3.setImageResource(fullStar)
            binding.rateStar4.setImageResource(fullStar)
            binding.rateStar5.setImageResource(emptyStar)
            starNumber=4
        }
        binding.rateStar5.setOnClickListener {
            //Toast.makeText(this,"kliknuta peta zvezdica",Toast.LENGTH_SHORT).show()
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
        request.enqueue(object : retrofit2.Callback<CommentSend?> {
            override fun onResponse(call: Call<CommentSend?>, response: Response<CommentSend?>) {
                if(response.isSuccessful){

                    var newComment=response.body()!!
                    requestGetComments(newComment)
                    binding.NewComment.text.clear()
                    hideKeyboard(binding.NewComment)
                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.message().toString())
                }


            }

            override fun onFailure(call: Call<CommentSend?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }
    fun requestGetComments(newComment:CommentSend?=null){
        if(newComment==null){
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
                                binding.tvCommentCount.text=countComments(comments!!).toString()
                            else
                                binding.tvCommentCount.text="12"
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
        else{
            (adapterComments as CommentsAdapter).items.add(0,newComment)
            recyclerViewComments?.adapter=adapterComments
            addedComment()
        }
    }

    fun requestAddRating(rating:RatingReceive){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", this@ActivitySinglePost)
        val request=postApi.addRating("Bearer "+token,post._id,rating)
        request.enqueue(object : retrofit2.Callback<RatingData?> {
            override fun onResponse(call: Call<RatingData?>, response: Response<RatingData?>) {
                if(response.isSuccessful){
                    var data=response.body()!!
                    binding.tvRating.text=String.format("%.2f",data.ratings)
                    binding.tvNumberOfRatings.text=String.format("%d",data.ratingscount)
                    Log.d("--------------",data.ratings.toString()+" "+data.ratingscount.toString())
                    when(data.myrating){
                        1->binding.rateStar1.performClick()
                        2->binding.rateStar2.performClick()
                        3->binding.rateStar3.performClick()
                        4->binding.rateStar4.performClick()
                        5->binding.rateStar5.performClick()
                        else->{
                            val emptyStar = R.drawable.empty_star
                            binding.rateStar1.setImageResource(emptyStar)
                            binding.rateStar2.setImageResource(emptyStar)
                            binding.rateStar3.setImageResource(emptyStar)
                            binding.rateStar4.setImageResource(emptyStar)
                            binding.rateStar5.setImageResource(emptyStar)
                        }
                    }
                    /*Toast.makeText(
                            this@ActivitySinglePost, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()*/
                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.errorBody().toString())
                }


            }

            override fun onFailure(call: Call<RatingData?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }

    private fun loadTextComponents() {
        binding.apply {
            tvTitle.text= post.location.name
            tvTitle.invalidate()
            tvLocationType.text="Otvorite Mapu"
            tvLocationType.setTextColor(Color.BLUE)
            tvLocationType.invalidate()
            tvLocationParent.text="TODO"
            tvLocationParent.invalidate()
            tvRating.text=post.ratings.toString()
            tvRating.invalidate()
            tvNumberOfRatings.text=post.ratingscount.toString()
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
        //binding.tvUser.text="proba"
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
    fun countComments(comments:List<CommentSend>):Int{
        var count:Int=0
        for(c in comments){
            if(c.replies!=null)
                count=count+countComments(c.replies!!)
            count=count+1
        }
        return count
    }

    public fun addedComment(){
        binding.tvCommentCount.text=(Integer.parseInt(binding.tvCommentCount.text.toString())+1).toString()
        binding.tvCommentCount.invalidate()
    }
}

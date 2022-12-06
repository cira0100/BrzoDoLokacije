package com.example.brzodolokacije.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Fragments.FragmentSinglePostComments
import com.example.brzodolokacije.Fragments.FragmentSinglePostDescription
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_single_post_description.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class ActivitySinglePost : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var layoutManagerImages: RecyclerView.LayoutManager? = null
    private var layoutManagerComments: RecyclerView.LayoutManager? = null
    private var adapterImages: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var adapterComments: RecyclerView.Adapter<CommentsAdapter.ViewHolder>? = null
    private var recyclerViewImages: RecyclerView?=null
    private var recyclerViewComments: RecyclerView?=null
    private var favouriteImage: ImageView?=null
    public  lateinit var post: PostPreview


    private var comments:MutableList<CommentSend>?=mutableListOf()
    private var starNumber:Number=0
    private lateinit var userData: UserReceive
    private lateinit var user: TextView
    private lateinit var linearLayout2: ConstraintLayout
    private lateinit var btnChangeHeightUp:ImageView
    private lateinit var btnChangeHeightDown:ImageView
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //get post --------------------------------
        post= intent.extras?.getParcelable("selectedPost")!!


        btnChangeHeightUp=findViewById(R.id.activitySinglePostChangeHeightUp)
        btnChangeHeightDown=findViewById(R.id.activitySinglePostChangeHeightDown)

        btnChangeHeightDown.isVisible=false
        btnChangeHeightDown.isGone=true
        btnChangeHeightDown.isClickable=false
        btnChangeHeightUp.isVisible=true
        btnChangeHeightUp.isGone=false
        btnChangeHeightUp.isClickable=true

        linearLayout2=findViewById(R.id.linearLayout2)

        linearLayout2.setOnClickListener {
            linearLayout2.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
        }


        //instantiate adapter and linearLayout
        adapterImages= PostImageAdapter(this@ActivitySinglePost, post.images as MutableList<PostImage>)
        layoutManagerImages= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewImages = binding.rvMain


        loadTextComponents()
        /*

        buildRecyclerViewComments()
        requestGetComments()
        favouriteImage=binding.ivFavourite
        // set recyclerView attributes
        recyclerViewImages?.setHasFixedSize(true)
        recyclerViewImages?.layoutManager = layoutManagerImages
        recyclerViewImages?.adapter = adapterImages

        setRatingListeners()
        translateOwnerIdToName(post.ownerId)
        loadFavourite()
        val alreadyrated= RatingReceive(starNumber.toInt(),post._id)
        requestAddRating(alreadyrated)
        */
        binding.tvUser.setOnClickListener {
            val intent: Intent = Intent(this@ActivitySinglePost,ActivityUserProfile::class.java)
            var args= Bundle()
            args.putString("post", Gson().toJson(post))
            this.startActivity(intent)
        }
        binding.tvLocationType.setOnClickListener{
            getMap()

        }


        btnChangeHeightUp.setOnClickListener {
            btnChangeHeightUp.isVisible=false
            btnChangeHeightUp.isGone=true
            btnChangeHeightUp.isClickable=false
            btnChangeHeightDown.isVisible=true
            btnChangeHeightDown.isGone=false
            btnChangeHeightDown.isClickable=true
            linearLayout2.setMinHeight(0);
            linearLayout2.setMinimumHeight(0);
            linearLayout2.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
        }
        btnChangeHeightDown.setOnClickListener {
            btnChangeHeightDown.isVisible=false
            btnChangeHeightDown.isGone=true
            btnChangeHeightDown.isClickable=false
            btnChangeHeightUp.isVisible=true
            btnChangeHeightUp.isGone=false
            btnChangeHeightUp.isClickable=true
            linearLayout2.setMinHeight(0);
            linearLayout2.setMinimumHeight(0);
            linearLayout2.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        /*favouriteImage!!.setOnClickListener{
            addRemoveFavourite()
        }
*/
        binding.btnActivitySinglePostDescription.setOnClickListener {
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            val fragment = FragmentSinglePostDescription()
            val b = Bundle()
            b.putString("post",  Gson().toJson(post))
            fragment.arguments = b
            fm.replace(R.id.flSinglePostFragmentContainer, fragment)
            fm.commit()
        }
        binding.btnActivitySinglePostComments.setOnClickListener{
            var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
            val fragment = FragmentSinglePostComments()
            val b = Bundle()
            b.putString("post",  Gson().toJson(post))
            fragment.arguments = b
            fm.replace(R.id.flSinglePostFragmentContainer, fragment)
            fm.commit()
        }


    }
    /*
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
    */

    fun getMap(){
        /*val mapDialogue = BottomSheetDialog(this@ActivitySinglePost, android.R.style.Theme_Black_NoTitleBar)
        mapDialogue.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
        mapDialogue.setContentView(R.layout.map_dialogue)
        mapDialogue.setCancelable(true)
        mapDialogue.setCanceledOnTouchOutside(true)*/
        var map: MapView? = null
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map=findViewById(R.id.MapDialogueMapView)
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
    /*
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

*/
}

package com.example.brzodolokacije.Activities

import LinePagerIndicatorDecoration
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.auth0.android.jwt.JWT
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Fragments.FragmentSinglePostComments
import com.example.brzodolokacije.Fragments.FragmentSinglePostDescription
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_single_post.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat


class ActivitySinglePost : AppCompatActivity(),OnRefreshListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var binding: ActivitySinglePostBinding
    private var layoutManagerImages: RecyclerView.LayoutManager? = null
    private var layoutManagerComments: RecyclerView.LayoutManager? = null
    private var adapterImages: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var adapterComments: RecyclerView.Adapter<CommentsAdapter.ViewHolder>? = null
    private var recyclerViewImages: RecyclerView?=null
    private var recyclerViewComments: RecyclerView?=null
    private var favouriteImage: ImageView?=null
    private lateinit var tagLayout: LinearLayout
    private lateinit var createdAt:TextView
    public  lateinit var post: PostPreview

    public lateinit var ratings:TextView
    public lateinit var ratingscount:TextView

    private var comments:MutableList<CommentSend>?=mutableListOf()
    private var starNumber:Number=0
    private lateinit var userData: UserReceive
    private lateinit var user: TextView
    private lateinit var linearLayout2: ConstraintLayout
    private lateinit var btnChangeHeightUp:ImageView
    private lateinit var btnChangeHeightDown:ImageView
    private lateinit var fragmentContainer: FrameLayout
    //private lateinit var detector: GestureDetectorCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //get post --------------------------------
        post= intent.extras?.getParcelable("selectedPost")!!
        addView()
        getMap()
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

        //detector= GestureDetectorCompat(this,SwipeGestureListener())

        //instantiate adapter and linearLayout
        adapterImages= PostImageAdapter(this@ActivitySinglePost, post.images as MutableList<PostImage>)
        layoutManagerImages= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewImages = binding.rvMain
        //tackice image
        var color = ContextCompat.getColor(this@ActivitySinglePost, R.color.unfollow)
        var color1 = ContextCompat.getColor(this@ActivitySinglePost, R.color.button_main)
        if(post.images.size>1)
            recyclerViewImages!!.addItemDecoration(LinePagerIndicatorDecoration(10,10,100,color,color1))

        //DODATI SLIKE
        recyclerViewImages?.setHasFixedSize(true)
        var snap: SnapHelper = PagerSnapHelper()
        snap.attachToRecyclerView(recyclerViewImages)
        recyclerViewImages?.layoutManager = layoutManagerImages
        recyclerViewImages?.adapter = adapterImages


        setUpFirstFragment()


        favouriteImage=binding.ivFavourite
        tagLayout =  binding.llTags


        // set recyclerView attributes



        translateOwnerIdToName(post.ownerId)

        binding.tvUser.setOnClickListener {
            val intent: Intent = Intent(this@ActivitySinglePost,ActivityUserProfile::class.java)
            var b= Bundle()
            intent.putExtra("user", Gson().toJson(userData))
            this.startActivity(intent)
        }

        //DATA CONTAINER PROMENA VISINE
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
            recyclerViewImages?.setHasFixedSize(true)
            recyclerViewImages?.layoutManager = layoutManagerImages
            recyclerViewImages?.adapter = adapterImages
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
            recyclerViewImages?.setHasFixedSize(true)
            recyclerViewImages?.layoutManager = layoutManagerImages
            recyclerViewImages?.adapter = adapterImages

        }

        favouriteImage!!.setOnClickListener{
            addRemoveFavourite()
        }

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

        swipeRefreshLayout = binding.PostSwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this@ActivitySinglePost)
        swipeRefreshLayout.setColorSchemeResources(
            R.color.purple_200,
            R.color.teal_200,
            R.color.dark_blue_transparent,
            R.color.purple_700
        )

        btnChangeHeightUp.performClick()
        btnChangeHeightDown.performClick()
    }

    /*override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(event?.let { detector.onTouchEvent(it) } == true){
            Log.d("testing swipeup","------------------------")
            true
        }
        else return super.onTouchEvent(event)
    }

    inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 20
        private val SWIPE_VELOCITY_THRESHOLD = 20
        override fun onFling(
            downEvent: MotionEvent,
            moveEvent: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            Log.d("testing swipeup","------------------------")
            var diffX = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0F
            var diffY = moveEvent?.y?.minus(downEvent!!.y) ?: 0.0F

            if (Math.abs(diffX) < Math.abs(diffY)) {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0 ) {
                        //top to bot

                    }
                    else {
                        //bot to top
                        btnChangeHeightUp.performClick()
                    }

                }
                return true
            }else{
                return super.onFling(downEvent, moveEvent, velocityX, velocityY)
            }
            return super.onFling(downEvent, moveEvent, velocityX, velocityY)
            }
    }*/

    fun setUpFirstFragment(){
        var fm: FragmentTransaction =supportFragmentManager.beginTransaction()
        val fragment = FragmentSinglePostDescription()
        val b = Bundle()
        b.putString("post",  Gson().toJson(post))
        fragment.arguments = b
        fm.replace(R.id.flSinglePostFragmentContainer, fragment)
        fm.commit()
    }

    override fun onRefresh(){
        onResume()
    }

    override fun onResume(){
        super.onResume()
        loadTextComponents()
        loadTags()
        loadFavourite()
        setUpFirstFragment()
        btnChangeHeightUp.performClick()
        btnChangeHeightDown.performClick()
        swipeRefreshLayout.isRefreshing=false
    }

    fun loadTags(){

        tagLayout.removeAllViews()
        if(post.tags!=null)
            for( item in post.tags!!){
                var newbtn = Button(this)
                newbtn.text = item
                var layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    50
                )
                layoutParams.setMargins(3)

                newbtn.layoutParams = layoutParams
                newbtn.setBackgroundColor(Color.parseColor("#1C789A"))
                newbtn.setTextColor(Color.WHITE)
                newbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
                newbtn.setPadding(3, 1, 3, 1)
                newbtn.isClickable = false
                tagLayout.addView(newbtn)
            }
        }

    public fun updateratings(rc:Int,r:Double){
        binding.tvRating.text=r.toString()
        binding.tvNumberOfRatings.text=rc.toString()
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
        var map: MapView? = null
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map=findViewById(R.id.MapDialogueMap)
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
            tvLocationType.text=post.location.country.toString()
            tvLocationType.invalidate()
            tvLocationParent.text=post.location.city.toString()
            tvLocationParent.invalidate()
            tvRating.text=post.ratings.toString()
            tvRating.invalidate()
            tvNumberOfRatings.text=post.ratingscount.toString()
            tvNumberOfRatings.invalidate()
            //tvRating.text=String.format("%.2f",data.ratings)
            //tvNumberOfRatings.text=String.format("%d",data.ratingscount)
            tvDatePosted.text=SimpleDateFormat("dd/MM/yyyy").format(post.createdAt)
            tvDatePosted.invalidate()

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
                                    response: Response<UserReceive>
            ) {
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

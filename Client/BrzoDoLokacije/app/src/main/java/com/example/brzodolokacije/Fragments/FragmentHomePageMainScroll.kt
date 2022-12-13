package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentHomePageMainScroll : Fragment(),OnRefreshListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var posts : MutableList<PostPreview>
    private lateinit var mostViewedPosts : MutableList<PostPreview>
    private lateinit var newestPosts : MutableList<PostPreview>
    private lateinit var bestRatedPosts:MutableList<PostPreview>
    private lateinit var rvPopular: RecyclerView
    private lateinit var rvNewest: RecyclerView
    private lateinit var rvBestRated: RecyclerView
    //NAVIGATION BUTTONS
    private lateinit var location_city: ImageButton
    private lateinit var location_beach: ImageButton
    private lateinit var location_mountain: ImageButton
    private lateinit var location_lake: ImageButton
    private lateinit var location_spa: ImageButton
    private lateinit var location_waterfall: ImageButton
    private lateinit var location_amusement_park: ImageButton
    private lateinit var location_attraction: ImageButton
    private lateinit var location_landmark: ImageButton
private lateinit var change:Button
    private lateinit var filter: LocationType
    private lateinit var filterString: String
    private lateinit var ll1: LinearLayout
    private lateinit var ll2:LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_home_page_main_scroll, container, false)



        rvPopular=view.findViewById(R.id.rvFragmentHomePagePopular)
        rvNewest=view.findViewById(R.id.rvFragmentHomePageNewest)
        rvBestRated=view.findViewById(R.id.rvFragmentHomePageBestRated)
        //change=view.findViewById(R.id.change)
        ll1=view.findViewById(R.id.ll1)
        ll2=view.findViewById(R.id.ll2)
        location_amusement_park=view.findViewById(R.id.btnFragmentHomePagelocation_amusement_park)
        location_attraction=view.findViewById(R.id.btnFragmentHomePagelocation_attraction)
        location_beach=view.findViewById(R.id.btnFragmentHomePagelocation_beach)
        location_lake=view.findViewById(R.id.btnFragmentHomePagelocation_lake)
        location_city=view.findViewById(R.id.btnFragmentHomePagelocation_city)
        location_landmark=view.findViewById(R.id.btnFragmentHomePagelocation_landmark)
        location_mountain=view.findViewById(R.id.btnFragmentHomePagelocation_mountain)
        location_spa=view.findViewById(R.id.btnFragmentHomePagelocation_spa)
        location_waterfall=view.findViewById(R.id.btnFragmentHomePagelocation_waterfall)

        //pokupi sve objave iz baze'
        //getAllPosts()

        var bundle=Bundle()
        var fragment=FragmentShowPostsByLocation()

        location_spa.setOnClickListener {
                tagSearch("Banja")




        }
        location_waterfall.setOnClickListener {
            tagSearch("Vodopad")


        }
        location_mountain.setOnClickListener {
            tagSearch("Planina")


        }
        location_landmark.setOnClickListener {
            tagSearch("Lokalitet")


        }
        location_city.setOnClickListener {
            tagSearch("Grad")

        }
        location_lake.setOnClickListener {
            tagSearch("Jezero")

        }
        location_attraction.setOnClickListener {
            tagSearch("Atrakcija")
        }
        location_amusement_park.setOnClickListener {
            tagSearch("Zabavni park")

        }
        location_beach.setOnClickListener {
            tagSearch("Plaza")

        }
       /* ll1.isVisible=true
        ll2.isVisible=false
        change.setOnClickListener {
            ll1.isVisible=true
            ll2.isVisible=false
        }

*/
        swipeRefreshLayout = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(
            R.color.purple_200,
            R.color.teal_200,
            R.color.dark_blue_transparent,
            R.color.purple_700
        )
        swipeRefreshLayout.post(kotlinx.coroutines.Runnable {
            swipeRefreshLayout.isRefreshing=true
        })
        return view
    }

    override fun onRefresh() {
        getAllPosts()
    }

    override fun onResume() {
        super.onResume()
        getAllPosts()
    }

    private fun getAllPosts(){
        val api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(RetrofitHelper.baseUrl)
            .build()
            .create(IBackendApi::class.java)
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.getPosts("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
//                    Toast.makeText(
//                        activity, "get all null", Toast.LENGTH_LONG
//                    ).show();

                    return
                }
                //refresh list
//                Toast.makeText(
//                    activity, "get all ", Toast.LENGTH_LONG
//                ).show();
                posts = response.body()!!.toMutableList<PostPreview>()
                getPopularPosts()
                getNewestPosts()
                getBestRatedPosts()
            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
//                Toast.makeText(
//                    activity,"nema objava", Toast.LENGTH_LONG
//                ).show();
            }
        })
    }

    private fun getPopularPosts(){//most viewed
//        Toast.makeText(
//            activity, "get all mv ", Toast.LENGTH_LONG
//        ).show();
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.get10MostViewed("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    return
                }
                var mostpopular = response.body()!!.toMutableList<PostPreview>()
                rvPopular.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= ShowPostsHomePageAdapter(mostpopular,requireActivity())

                }
            }
            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {

            }
        })


    }
    private fun getNewestPosts(){
//        Toast.makeText(
//            activity, "get all r ", Toast.LENGTH_LONG
//        ).show();

        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.get10Newest("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    return
                }
                var newestposts = response.body()!!.toMutableList<PostPreview>()
                rvNewest.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= ShowPostsHomePageAdapter(newestposts,requireActivity())
                }
                swipeRefreshLayout.isRefreshing=false
            }
            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {

            }
        })

    }

    private fun getBestRatedPosts(){
//        Toast.makeText(
//            activity, "get all br ", Toast.LENGTH_LONG
//        ).show();
        val api = RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val data=api.get10Best("Bearer "+token)

        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    return
                }
                var bestposts = response.body()!!.toMutableList<PostPreview>()
                rvBestRated.apply {
                    layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                    adapter= ShowPostsHomePageAdapter(bestposts,requireActivity())
                }
            }
            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {

            }
        })


    }
    private fun tagSearch(tag:String){
        var act = requireActivity() as NavigationActivity
        act.searchQuery = tag
        act.searchId = ""
        act.bottomNav.selectedItemId = R.id.navAllPosts
    }

}
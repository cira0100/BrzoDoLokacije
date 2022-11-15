package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class FragmentHomePageMainScroll : Fragment() {

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

    private lateinit var filter: LocationType
    private lateinit var filterString: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_home_page_main_scroll, container, false)



        rvPopular=view.findViewById(R.id.rvFragmentHomePagePopular)
        rvNewest=view.findViewById(R.id.rvFragmentHomePageNewest)
        rvBestRated=view.findViewById(R.id.rvFragmentHomePageBestRated)

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
        getAllPosts()

        var bundle=Bundle()
        var fragment=FragmentShowPostsByLocation()

        location_spa.setOnClickListener {
            Toast.makeText(
                activity, "BANJAAAAAAAAAAAAAAA", Toast.LENGTH_LONG
            ).show();
            filter=LocationType.BANJA
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle

            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()


        }
        location_waterfall.setOnClickListener {
            filter=LocationType.VODOPAD
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle

            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()


        }
        location_mountain.setOnClickListener {
            filter=LocationType.PLANINA
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle

            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()


        }
        location_landmark.setOnClickListener {
            filter=LocationType.LOKALITET
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle

            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()


        }
        location_city.setOnClickListener {
            filter=LocationType.GRAD
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle

            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()

        }
        location_lake.setOnClickListener {
            filter=LocationType.JEZERO
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle
            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()

        }
        location_attraction.setOnClickListener {
            filter=LocationType.ATRAKCIJA
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle
            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()
        }
        location_amusement_park.setOnClickListener {
            filter=LocationType.ZABAVNI_PARK
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle
            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()

        }
        location_beach.setOnClickListener {
            filter=LocationType.PLAZA
            filterString=filter.toString()
            bundle.putString("data",filterString)
            fragment.arguments=bundle
            val parentFrag: FragmentHomePage = this@FragmentHomePageMainScroll.getParentFragment() as FragmentHomePage
            parentFrag.changeScrollVIewToLocationView()
            parentFrag.setBtnBackVisible()

        }



        return view
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
                getPopularPosts(posts)
                getNewestPosts(posts)
                getBestRatedPosts(posts)
            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
//                Toast.makeText(
//                    activity,"nema objava", Toast.LENGTH_LONG
//                ).show();
            }
        })
    }

    private fun getPopularPosts(allPosts:MutableList<PostPreview>){//most viewed
//        Toast.makeText(
//            activity, "get all mv ", Toast.LENGTH_LONG
//        ).show();
        mostViewedPosts=allPosts
        mostViewedPosts.sortByDescending { it.views }
        rvPopular.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
            adapter= ShowPostsHomePageAdapter(mostViewedPosts,requireActivity())

        }

    }
    private fun getNewestPosts(allPosts:MutableList<PostPreview>){
//        Toast.makeText(
//            activity, "get all r ", Toast.LENGTH_LONG
//        ).show();
        newestPosts=allPosts/// izmeniti nakon dodavanja datuma u model!!!!!!
        newestPosts.sortBy { it.ratings}
        rvNewest.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
            adapter= ShowPostsHomePageAdapter(newestPosts,requireActivity())
        }
    }

    private fun getBestRatedPosts(allPosts:MutableList<PostPreview>){
//        Toast.makeText(
//            activity, "get all br ", Toast.LENGTH_LONG
//        ).show();
        bestRatedPosts=allPosts
        bestRatedPosts.sortByDescending { it.ratings }
        rvBestRated.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
            adapter= ShowPostsHomePageAdapter(bestRatedPosts,requireActivity())
        }

    }

}
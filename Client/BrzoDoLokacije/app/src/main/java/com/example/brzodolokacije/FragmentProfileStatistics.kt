package com.example.brzodolokacije

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.MonthViewsAdapter
import com.example.brzodolokacije.Adapters.MyPostsAdapter
import com.example.brzodolokacije.Models.MonthlyViews
import com.example.brzodolokacije.Models.Statistics
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Response


class FragmentProfileStatistics : Fragment() {

    private var stats:Statistics?=null
    private var username:String?=null
    private lateinit var totalViews:TextView
    private  lateinit var numberOfRatings:TextView
    private  lateinit var averageRatings:TextView
    private  lateinit var numberOfFavourite:TextView
    private  lateinit var rcMonths:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_profile_statistics, container, false)
        username=this.requireArguments().getString("username")
        totalViews=view.findViewById(R.id.tvProfileStatisticsViews)
        numberOfRatings=view.findViewById(R.id.tvProfileStatisticsRatingNumber)
        averageRatings=view.findViewById(R.id.tvProfileStatisticsAverageRating)
        numberOfFavourite=view.findViewById(R.id.tvProfileStatisticsFavouriteNumber)
        rcMonths=view.findViewById(R.id.rvFragmentProfileStatisticsMonths)



        loadStats()

        return view
    }


    fun loadStats(){
        val authApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request=authApi.getUserStatsFromUsername("Bearer "+token,username!!)

        request.enqueue(object : retrofit2.Callback<Statistics?> {
            override fun onResponse(call: Call<Statistics?>, response: Response<Statistics?>) {
                if(response.isSuccessful()){
                    stats=response.body()
                    loadText()
                    loadMonths()


                }
            }
            override fun onFailure(call: Call<Statistics?>, t: Throwable) {
                Toast.makeText(
                    activity, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })

    }
    fun loadText(){
        totalViews.text=stats!!.totalViews.toString()
        numberOfRatings.text=stats!!.numberOfRatingsOnPosts.toString()
        averageRatings.text=stats!!.averagePostRatingOnPosts.toString()
        numberOfFavourite.text=stats!!.numberOfFavouritePosts.toString()
    }
    private fun loadMonths(){
        rcMonths.apply {
            layoutManager= GridLayoutManager(requireContext(),2)
            adapter= MonthViewsAdapter(requireActivity(),
                stats!!.monthlyViews as MutableList<MonthlyViews>
            )

        }
    }


}
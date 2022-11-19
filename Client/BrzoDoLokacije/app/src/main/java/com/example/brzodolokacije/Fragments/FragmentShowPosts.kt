package com.example.brzodolokacije.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.paging.cachedIn
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.SampleAdapter
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.*
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.FragmentHomeBinding
import com.example.brzodolokacije.databinding.FragmentShowPostsBinding
import com.example.brzodolokacije.paging.SearchPostsRepository
import com.example.brzodolokacije.paging.SearchPostsViewModel
import com.example.brzodolokacije.paging.SearchPostsViewModelFactory
import kotlinx.android.synthetic.main.fragment_show_posts.*
import kotlinx.android.synthetic.main.fragment_show_posts.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class FragmentShowPosts : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentShowPostsBinding
    private var linearManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: ShowPostsAdapter? = null
    private var recyclerView: RecyclerView?=null
    private var gridManagerVar: RecyclerView.LayoutManager?=null
    private var swipeRefreshLayout:SwipeRefreshLayout?=null
    private lateinit var searchPostsViewModel:SearchPostsViewModel
    private var searchParams:SearchParams?= SearchParams("6375784fe84e2d53df32bf03",1,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding=FragmentShowPostsBinding.inflate(layoutInflater)
        //instantiate adapter and linearLayout
        adapterVar=ShowPostsAdapter(requireActivity())
        linearManagerVar= LinearLayoutManager(activity)
        gridManagerVar=GridLayoutManager(activity,2)
    }

    private fun setUpViewModel() {
        val factory=SearchPostsViewModelFactory(RetrofitHelper.getInstance(),requireActivity())
        searchPostsViewModel=ViewModelProvider(this@FragmentShowPosts,factory).get(SearchPostsViewModel::class.java)
    }

    fun setUpListeners(rootView: View?){
        rootView?.findViewById<ImageButton>(R.id.btnGridLayout)?.setOnClickListener() {
            if(recyclerView?.layoutManager!=gridManagerVar){
                recyclerView?.layoutManager=gridManagerVar
            }
            Log.d("main","klik")
        }

        rootView?.findViewById<ImageButton>(R.id.btnLinearLayout)?.setOnClickListener() {
            if(recyclerView?.layoutManager!=linearManagerVar){
                recyclerView?.layoutManager=linearManagerVar
            }
            Log.d("main","klik")
        }
    }

    fun requestToBack(searchParams: SearchParams){
        lifecycleScope.launch{
            searchPostsViewModel.fetchPosts(searchParams).distinctUntilChanged().collectLatest {
                adapterVar?.submitData(lifecycle,it)
                swipeRefreshLayout?.isRefreshing=false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_show_posts, container, false)
        recyclerView = rootView?.findViewById(R.id.rvMain)
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        //recyclerView?.layoutManager = linearManagerVar
        recyclerView?.layoutManager = linearManagerVar
        recyclerView?.adapter = adapterVar
        setUpListeners(rootView)
        swipeRefreshLayout = rootView?.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.setColorSchemeResources(
            R.color.purple_200,
            R.color.teal_200,
            R.color.dark_blue_transparent,
            R.color.purple_700
        )
        swipeRefreshLayout?.post(kotlinx.coroutines.Runnable {
            swipeRefreshLayout?.isRefreshing=true
            requestToBack(searchParams!!)
        })
        return rootView
    }

    override fun onRefresh() {
        requestToBack(searchParams!!)
    }

}
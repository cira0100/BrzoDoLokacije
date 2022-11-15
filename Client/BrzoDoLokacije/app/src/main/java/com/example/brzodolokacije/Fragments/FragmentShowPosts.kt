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
import kotlinx.android.synthetic.main.fragment_show_posts.*
import kotlinx.android.synthetic.main.fragment_show_posts.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class FragmentShowPosts : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentShowPostsBinding
    private var posts : MutableList<PostPreview> = mutableListOf()
    private var linearManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null
    private var gridManagerVar: RecyclerView.LayoutManager?=null
    private var swipeRefreshLayout:SwipeRefreshLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragmentShowPostsBinding.inflate(layoutInflater)
        //instantiate adapter and linearLayout
        adapterVar=ShowPostsAdapter(requireActivity(),posts)
        linearManagerVar= LinearLayoutManager(activity)
        gridManagerVar=GridLayoutManager(activity,2)
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

    fun requestToBack(){
        val postApi= RetrofitHelper.getInstance()
        val token=SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request=postApi.getPosts("Bearer "+token)
        request.enqueue(object : retrofit2.Callback<MutableList<PostPreview>?> {
            override fun onResponse(call: Call<MutableList<PostPreview>?>, response: Response<MutableList<PostPreview>?>) {
                if(response.isSuccessful){
                    posts=response.body()!!
                    recyclerView?.adapter=ShowPostsAdapter(requireActivity(),posts)
                    Toast.makeText(
                        activity, "prosao zahtev", Toast.LENGTH_LONG
                    ).show()
                    swipeRefreshLayout?.isRefreshing=false
                }else{
                    if(response.errorBody()!=null)
                        Toast.makeText(activity, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                }


            }

            override fun onFailure(call: Call<MutableList<PostPreview>?>, t: Throwable) {
                Toast.makeText(
                    activity, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })
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
            requestToBack()
        })
        return rootView
    }

    override fun onRefresh() {
        requestToBack()
    }

}
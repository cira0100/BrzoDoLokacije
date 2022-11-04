package com.example.brzodolokacije.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.SampleAdapter
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.ListItemModel
import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.FragmentHomeBinding


class FragmentShowPosts : Fragment() {

    private lateinit var binding: FragmentShowPosts
    private var posts : MutableList<Post> = mutableListOf()
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //load data for the list
        loadData()
        //instantiate adapter and linearLayout
        adapterVar=ShowPostsAdapter(posts)
        layoutManagerVar= LinearLayoutManager(activity)
    }

    private fun loadData() {
        posts.add(Post("123",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid","opasdiad",
            mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf()))
        posts.add(Post("123",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid","opasdiad",
            mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf()))
        posts.add(Post("123",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid","opasdiad",
            mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf()))
        posts.add(Post("123",
            Location("asd","Ajfelov toranj","Pariz",
                "Francuska","idk",1.1,1.1, LocationType.GRAD),"opsiopsaid","opasdiad",
            mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_show_posts, container, false)
        recyclerView = rootView?.findViewById(R.id.rvMain)
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        return rootView
    }

}
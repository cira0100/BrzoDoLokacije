package com.example.brzodolokacije.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.FragmentPostBinding


class FragmentPost : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private var images : MutableList<java.io.File> = mutableListOf()
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //load data for the list
        loadData()
        //instantiate adapter and linearLayout
        adapterVar=PostImageAdapter(images)
        layoutManagerVar= LinearLayoutManager(activity)
    }

    private fun loadData() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_post, container, false)
        recyclerView = rootView?.findViewById(R.id.rvMain)
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        return rootView
    }

}
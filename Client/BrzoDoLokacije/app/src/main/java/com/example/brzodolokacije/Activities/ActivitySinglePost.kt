package com.example.brzodolokacije.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.PostImageAdapter
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.ActivitySinglePostBinding

class ActivitySinglePost : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var images : MutableList<java.io.File> = mutableListOf()
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<PostImageAdapter.ViewHolder>? = null
    private var recyclerView: RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)
        //load data for the list
        loadData()
        //instantiate adapter and linearLayout
        adapterVar= PostImageAdapter(images)
        layoutManagerVar= LinearLayoutManager(this)
        recyclerView = binding.rvMain
        // set recyclerView attributes
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
    }

    private fun loadData() {
    }

}
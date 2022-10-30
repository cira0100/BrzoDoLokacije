package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.SampleAdapter
import com.example.brzodolokacije.Models.ListItemModel
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHome : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var nameList : MutableList<ListItemModel> = mutableListOf()
    private var layoutManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: RecyclerView.Adapter<SampleAdapter.ViewHolder>? = null
    private var recyclerView:RecyclerView?=null

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        Log.d("Main","blabla")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //load data for the list
        loadData()
        adapterVar=SampleAdapter(nameList)
        layoutManagerVar=LinearLayoutManager(activity)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_home, container, false)
        recyclerView = rootView?.findViewById(R.id.rvMain)
        // rest of my stuff
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManagerVar
        recyclerView?.adapter = adapterVar
        return rootView
    }
    fun loadData(){
        nameList.add(ListItemModel(1,"Sample 1"))
        nameList.add(ListItemModel(2,"Sample 2"))
        nameList.add(ListItemModel(3,"Sample 3"))
        nameList.add(ListItemModel(4,"Sample 4"))
        nameList.add(ListItemModel(5,"Sample 5"))
        nameList.add(ListItemModel(6,"Sample 6"))
    }
}

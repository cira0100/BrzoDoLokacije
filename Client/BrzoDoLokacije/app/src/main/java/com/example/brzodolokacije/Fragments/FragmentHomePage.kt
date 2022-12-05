package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.SearchParams
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.RetrofitHelper.baseUrl
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FragmentHomePage : Fragment() {

    private lateinit var btnChat:ImageView
    private lateinit var btnBack:ImageView
    private lateinit var searchBar:AutoCompleteTextView
    private lateinit var searchButton: MaterialButton
    var responseLocations:MutableList<com.example.brzodolokacije.Models.Location>?=null
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

     }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_home_page, container, false)
        btnBack=view.findViewById(R.id.btnFragmentHomePageBack)
        btnChat=view.findViewById(R.id.ivFragmentHomePageChat)
        searchBar=view.findViewById(R.id.etFragmentHomePageSearch)
        searchButton=view.findViewById(R.id.mbFragmentHomePageSearchButton)
        setBtnBackInvisible()
        setUpSpinner()
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentHomePageMainScroll())
        fm.commit()

        btnBack.setOnClickListener{
            changeLocationViewToScrollView()
            setBtnBackInvisible()
        }

        btnChat.setOnClickListener {
            val intent: Intent = Intent(activity, ChatActivity::class.java)
            requireActivity().startActivity(intent)
        }
        searchButton.setOnClickListener{
            searchText()
        }
        searchBar.addTextChangedListener{
            onTextEnter()
        }
        searchBar.setOnKeyListener(View.OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                searchText()
                return@OnKeyListener true
            }
            false
        })

        return view
    }


    fun searchText(){
        if(searchBar.text==null || searchBar.text.toString().trim()=="")
            return

        var act=requireActivity() as NavigationActivity
        act.searchQuery=searchBar.text.toString()
        act.searchId=""
        act.bottomNav.selectedItemId=R.id.navAllPosts
    }
    fun changeScrollVIewToLocationView(){
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentShowPostsByLocation())
        fm.commit()
    }
    fun changeLocationViewToScrollView(){
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()
        fm.replace(R.id.flFragmentHomePageMainContent, FragmentHomePageMainScroll())
        fm.commit()
    }
    fun setBtnBackInvisible(){
        btnBack.isVisible=false
    }
    fun setBtnBackVisible(){
        btnBack.isVisible=true
    }
    fun onTextEnter(){
        var api= RetrofitHelper.getInstance()
        var jwtString= SharedPreferencesHelper.getValue("jwt",requireActivity())
        var text=searchBar.text
        Log.d("test",text.toString())
        if(text==null ||text.toString().trim()=="")
            return
        var data=api.searchLocationsQuery("Bearer "+jwtString,text.toString())
        data.enqueue(object : retrofit2.Callback<MutableList<com.example.brzodolokacije.Models.Location>> {
            override fun onResponse(call: Call<MutableList<Location>?>, response: Response<MutableList<Location>>) {
                if(response.isSuccessful){
                    var existingLocation=responseLocations
                    responseLocations=response.body()!!
                    if(existingLocation!=null && existingLocation.size>0)
                        for(loc in existingLocation!!){
                            spinnerAdapter!!.remove(loc.name)
                        }
                    for(loc in responseLocations!!){
                        spinnerAdapter!!.add(loc.name)
                    }
                    spinnerAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<MutableList<Location>>, t: Throwable) {

            }
        })


    }
    var arraySpinner :MutableList<String>?=null
    var spinnerAdapter: ArrayAdapter<String>?=null

    fun setUpSpinner() {
        arraySpinner=mutableListOf<String>()
        spinnerAdapter= ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1, arraySpinner!!)
        searchBar.threshold=1
        searchBar.setAdapter(spinnerAdapter)
        searchBar.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            var selectedLocation = responseLocations!!.find { location -> location.name == selected }

            var act=requireActivity() as NavigationActivity
            act.searchQuery=selectedLocation!!.name
            act.searchId=selectedLocation!!._id
            act.bottomNav.selectedItemId=R.id.navAllPosts


        })


    }
}

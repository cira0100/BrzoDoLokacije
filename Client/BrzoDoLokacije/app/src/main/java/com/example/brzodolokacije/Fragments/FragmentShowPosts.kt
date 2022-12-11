package com.example.brzodolokacije.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.SearchParams
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.FragmentShowPostsBinding
import com.example.brzodolokacije.paging.SearchPostsViewModel
import com.example.brzodolokacije.paging.SearchPostsViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_show_posts.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import retrofit2.Call
import retrofit2.Response


class FragmentShowPosts : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentShowPostsBinding
    private var linearManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: ShowPostsAdapter? = null
    private var recyclerView: RecyclerView?=null
    private var gridManagerVar: RecyclerView.LayoutManager?=null
    private var swipeRefreshLayout:SwipeRefreshLayout?=null
    private lateinit var searchButton: MaterialButton
    private lateinit var searchPostsViewModel:SearchPostsViewModel
    private var searchParams:SearchParams?= SearchParams("-1",1,1)
    private lateinit var btnFilter:ImageButton
    private lateinit var btnSort:ImageButton
    private lateinit var searchBar: AutoCompleteTextView
    var responseLocations:MutableList<com.example.brzodolokacije.Models.Location>?=null
    var selectedLocation:com.example.brzodolokacije.Models.Location?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding=FragmentShowPostsBinding.inflate(layoutInflater)
        //instantiate adapter and linearLayout
        adapterVar=ShowPostsAdapter(requireActivity())
        linearManagerVar= LinearLayoutManager(activity)
        gridManagerVar=GridLayoutManager(activity,2)
    }
    fun searchText(){
        if(searchBar.text==null || searchBar.text.toString().trim()=="")
            return
        var act=requireActivity() as NavigationActivity
        act.searchQuery=searchBar.text.toString()
        act.searchId=""
        searchParams=SearchParams(searchBar.text.toString(),1,1)
        requestToBack(searchParams!!)
    }
    fun onTextEnter(){
        var api=RetrofitHelper.getInstance()
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
            selectedLocation = responseLocations!!.find { location -> location.name == selected }
            var act=requireActivity() as NavigationActivity
            act.searchQuery=selectedLocation!!.name
            act.searchId=selectedLocation!!._id
            searchParams=SearchParams(selectedLocation!!._id,1,1)//to do sort type
            requestToBack(searchParams!!)

        })


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

        btnFilter=rootView.findViewById(R.id.btnSortType)
        btnSort=rootView.findViewById(R.id.btnSortDirection)

        btnFilter.setOnClickListener{
            showBottomSheetFilter()
        }

        btnSort.setOnClickListener{
            showBottomSheetSort()
        }
        searchBar=rootView.findViewById(R.id.etFragmentShowPostsSearch) as AutoCompleteTextView
        searchButton=rootView.findViewById<View>(R.id.mbFragmentHomePageSearch) as MaterialButton
        setUpSpinner()
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
        refreshSearch()


        return rootView
    }

    override fun onRefresh() {
        requestToBack(searchParams!!)
        refreshSearch()
    }
    override fun onResume() {
        super.onResume()
        refreshSearch()

    }
    private fun refreshSearch(){
        var act=requireActivity() as NavigationActivity
        Log.d("TEST","USAO")
        if(act.searchId!=null && act.searchId.trim()!="")
        {
            searchBar.setText(act.searchQuery,TextView.BufferType.EDITABLE)
            searchParams= SearchParams(act.searchId,1,1)
            requestToBack(searchParams!!)
        }else
            if(act.searchQuery!=null && act.searchQuery.trim()!="")
            {
                searchBar.setText(act.searchQuery,TextView.BufferType.EDITABLE)
                searchParams= SearchParams(act.searchQuery,1,1)
                requestToBack(searchParams!!)
            }
    }


    private fun showBottomSheetFilter() {
        var bottomSheetDialog: BottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter)
        bottomSheetDialog.show()

        var dateFrom=bottomSheetDialog.findViewById<View>(R.id.dateFromBSF)as EditText
        var dateTo=bottomSheetDialog.findViewById<View>(R.id.dateToBSF) as EditText
        var location=bottomSheetDialog.findViewById<View>(R.id.locationBSF) as EditText
        var filter = bottomSheetDialog.findViewById<View>(R.id.btnBSFFilter) as Button


        filter.setOnClickListener {
            //povezati sa back-om


        }
    }
    private fun showBottomSheetSort() {
        var bottomSheetDialogSort: BottomSheetDialog
        bottomSheetDialogSort = BottomSheetDialog(requireContext())
        bottomSheetDialogSort.setContentView(R.layout.bottom_sheet_sort)
        bottomSheetDialogSort.show()

    }
}
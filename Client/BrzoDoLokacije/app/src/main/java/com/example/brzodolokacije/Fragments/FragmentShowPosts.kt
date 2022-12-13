package com.example.brzodolokacije.Fragments


import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.Activities.NavigationActivity
import com.example.brzodolokacije.Adapters.ShowPostsAdapter
import com.example.brzodolokacije.Adapters.ShowPostsGridViewAdapter

import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Models.SearchParams
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.FragmentShowPostsBinding
import com.example.brzodolokacije.paging.SearchPostsViewModel
import com.example.brzodolokacije.paging.SearchPostsViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_splash_page.*
import kotlinx.android.synthetic.main.bottom_sheet_sort.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FragmentShowPosts : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var dataChanged:Boolean=false
    private var flowData: PagingData<PostPreview>?=null
    private lateinit var binding: FragmentShowPostsBinding
    private var linearManagerVar: RecyclerView.LayoutManager? = null
    private var adapterVar: ShowPostsAdapter? = null
    private var gridViewAdapter:ShowPostsGridViewAdapter?=null
    private var recyclerView: RecyclerView?=null
   // private var gridManagerVar: RecyclerView.LayoutManager?=null
    private var swipeRefreshLayout:SwipeRefreshLayout?=null
    private lateinit var searchButton: MaterialButton
    private lateinit var searchPostsViewModel:SearchPostsViewModel

    private lateinit var btnFilter:ImageButton
    private lateinit var btnSort:ImageButton
    private lateinit var searchBar: AutoCompleteTextView
    var responseLocations:MutableList<com.example.brzodolokacije.Models.Location>?=null
    var selectedLocation:com.example.brzodolokacije.Models.Location?=null


    private lateinit var filter:Button
    private lateinit var sort:Button

    private var filterBool:Boolean=false
    private var ratingFrom:Int=-1
    private var ratingTo:Int=-1
    private var viewsFrom:Int=-1
    private var viewsTo:Int=-1
    private var searchParams:SearchParams?= SearchParams("-1",filterBool,1,1,ratingFrom,ratingTo,viewsFrom,viewsTo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding=FragmentShowPostsBinding.inflate(layoutInflater)
        //instantiate adapter and linearLayout
        adapterVar=ShowPostsAdapter(requireActivity())
        linearManagerVar= LinearLayoutManager(activity)
        //gridManagerVar=GridLayoutManager(activity,2)


    }
    fun searchText(){
        if(searchBar.text==null || searchBar.text.toString().trim()=="")
            return
        var act=requireActivity() as NavigationActivity
        act.searchQuery=searchBar.text.toString()
        act.searchId=""
        searchParams=SearchParams(searchBar.text.toString(),filterBool,1,1,ratingFrom,ratingTo,viewsFrom,viewsTo)
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
            searchParams=SearchParams(selectedLocation!!._id,filterBool,1,1,ratingFrom,ratingTo,viewsFrom,viewsTo)//to do sort type
            requestToBack(searchParams!!)

        })


    }
    private fun setUpViewModel() {
        val factory=SearchPostsViewModelFactory(RetrofitHelper.getInstance(),requireActivity())
        searchPostsViewModel=ViewModelProvider(this@FragmentShowPosts,factory).get(SearchPostsViewModel::class.java)
    }

    fun setUpListeners(rootView: View?){
        rootView?.findViewById<ImageButton>(R.id.btnGridLayout)?.setOnClickListener() {
            /*if(recyclerView?.layoutManager!=gridManagerVar){
                recyclerView?.layoutManager=gridManagerVar*/
            recyclerView?.apply {
                layoutManager= GridLayoutManager(activity,2)
                if(gridViewAdapter==null)
                    gridViewAdapter= ShowPostsGridViewAdapter(requireActivity())
                recyclerView?.adapter=gridViewAdapter
                if(dataChanged)
                    gridViewAdapter?.submitData(lifecycle,flowData!!)
                dataChanged=false
            }
            Log.d("main","klik")
        }

        rootView?.findViewById<ImageButton>(R.id.btnLinearLayout)?.setOnClickListener() {
            if(recyclerView?.layoutManager!=linearManagerVar){
                recyclerView?.layoutManager=linearManagerVar
            }
            recyclerView?.adapter=adapterVar
            if(dataChanged)
                adapterVar?.submitData(lifecycle,flowData!!)
            dataChanged=false
            Log.d("main","klik")
        }


    }

    fun requestToBack(searchParams: SearchParams){
        lifecycleScope.launch{
            searchPostsViewModel.fetchPosts(searchParams).distinctUntilChanged().collectLatest {
                if(recyclerView?.adapter == gridViewAdapter){
                    gridViewAdapter?.submitData(lifecycle,it)
                }
                else{
                    adapterVar?.submitData(lifecycle,it)
                }
                dataChanged=true
                flowData=it
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
//        recyclerView?.setHasFixedSize(true)
        //recyclerView?.layoutManager = linearManagerVar
        recyclerView?.layoutManager = linearManagerVar
        recyclerView?.adapter = adapterVar


        //filter dialog
        var bottomSheetDialogFilter: BottomSheetDialog
        bottomSheetDialogFilter = BottomSheetDialog(requireContext())
        bottomSheetDialogFilter.setContentView(R.layout.bottom_sheet_filter)

        //sort dialog
        var bottomSheetDialogSort: BottomSheetDialog
        bottomSheetDialogSort = BottomSheetDialog(requireContext())
        bottomSheetDialogSort.setContentView(R.layout.bottom_sheet_sort)

        var ratingFromInput=bottomSheetDialogFilter.findViewById<View>(R.id.filterRatingFrom) as EditText
        var ratingToInput=bottomSheetDialogFilter.findViewById<View>(R.id.filterRatingTo) as EditText
        var viewsFromInput=bottomSheetDialogFilter.findViewById<View>(R.id.filterViewsFrom) as EditText
        var viewsToInput=bottomSheetDialogFilter.findViewById<View>(R.id.filterViewsTo) as EditText


        btnFilter= rootView!!.findViewById(R.id.btnSortType)
        btnSort=rootView!!.findViewById(R.id.btnSortDirection)

        btnFilter.setOnClickListener{
            bottomSheetDialogFilter.show()

            var filter = bottomSheetDialogFilter.findViewById<View>(R.id.btnBSFFilter) as Button
            var radioGroupF = bottomSheetDialogFilter.findViewById<View>(R.id.radioGroupFilter) as RadioGroup

            filter.setOnClickListener {

                var selectedRadioButtonIdF: Int = radioGroupF.checkedRadioButtonId
                if (selectedRadioButtonIdF != -1) {
                    var selectedRadioButtonF =
                        bottomSheetDialogFilter.findViewById<View>(selectedRadioButtonIdF) as RadioButton
                    val string: String = selectedRadioButtonF.text.toString().trim()

                    if (string.equals("Prethodna nedelja")) {
                        searchParams!!.filterdate= 5
                    } else if (string.equals("Prethodni mesec")) {
                        searchParams!!.filterdate=4
                    } else if (string.equals("Prethodna tri meseca")) {
                        searchParams!!.filterdate=3
                    } else if (string.equals("Prethodna godina")) {
                        searchParams!!.filterdate=2
                    } else {
                        searchParams!!.filterdate=1
                    }
                }
                if(ratingFromInput.text.toString().isNotEmpty()) {
                    if (ratingFromInput.text.toString().trim().toInt() >= 0) {
                        filterBool = true
                        ratingFrom = ratingFromInput.text.toString().toInt()
                    } else {
                        Toast.makeText(
                            activity,
                            "Vrednost rejtinga ne može biti negativna",
                            Toast.LENGTH_LONG
                        ).show();
                        var fromrating =
                            bottomSheetDialogFilter.findViewById<View>(R.id.ratingFromtxt) as TextView
                        fromrating.setTextColor(Color.RED)
                    }
                }
                if(ratingToInput.text.toString().isNotEmpty()) {
                    if (ratingToInput.text.toString().trim().toInt() >= 0) {
                        filterBool = true
                        ratingTo = ratingToInput.text.toString().toInt()
                    } else {
                        Toast.makeText(
                            activity,
                            "Vrednost rejtinga ne može biti negativna",
                            Toast.LENGTH_LONG
                        ).show();
                        var torating =
                            bottomSheetDialogFilter.findViewById<View>(R.id.ratingTotxt) as TextView
                        torating.setTextColor(Color.RED)
                    }
                }
                if(viewsFromInput.text.toString().isNotEmpty()) {
                    if (viewsFromInput.text.toString().trim().toInt() >= 0) {
                        filterBool = true
                        viewsFrom = viewsFromInput.text.toString().toInt()
                    } else {
                        Toast.makeText(
                            activity,
                            "Vrednost broja pregleda ne može biti negativna",
                            Toast.LENGTH_LONG
                        ).show();
                        var fromviews =
                            bottomSheetDialogFilter.findViewById<View>(R.id.viewsFromtxt) as TextView
                        fromviews.setTextColor(Color.RED)
                    }
                }
                if(viewsToInput.text.toString().isNotEmpty()) {
                    if (viewsToInput.text.toString().trim().toInt() >= 0) {
                        filterBool = true
                        viewsTo = viewsToInput.text.toString().trim().toInt()
                    } else {
                        Toast.makeText(
                            activity,
                            "Vrednost broja pregleda ne može biti negativna",
                            Toast.LENGTH_LONG
                        ).show();
                        var toviews =
                            bottomSheetDialogFilter.findViewById<View>(R.id.viewsTotxt) as TextView
                        toviews.setTextColor(Color.RED)
                    }
                }
                searchParams!!.filter=filterBool
                searchParams!!.ratingFrom=ratingFrom
                searchParams!!.ratingTo=ratingTo
                searchParams!!.viewsFrom=viewsFrom
                searchParams!!.viewsTo=viewsTo

                bottomSheetDialogFilter.dismiss()
            }


        }
        btnSort.setOnClickListener{
            bottomSheetDialogSort.show()
            var sort = bottomSheetDialogSort.findViewById<View>(R.id.btnSortPosts) as Button
            var radioGroup = bottomSheetDialogSort.findViewById<View>(R.id.radioGroup)as RadioGroup

            sort.setOnClickListener {
                val selectedRadioButtonId: Int = radioGroup.checkedRadioButtonId
                if (selectedRadioButtonId != -1) {
                    var selectedRadioButton =
                        bottomSheetDialogSort.findViewById<View>(selectedRadioButtonId) as RadioButton
                    val string: String = selectedRadioButton.text.toString().trim()
                    if (string.equals("Najnovije")) {
                        searchParams!!.sorttype = 3
                    } else if (string.equals("Najstarije")) {
                        searchParams!!.sorttype=4
                    } else if (string.equals("Najbolje ocenjeno")) {
                       searchParams!!.sorttype=2
                    } else if (string.equals("Najviše pregleda")) {
                        searchParams!!.sorttype=1
                    } else {
                        searchParams!!.sorttype=1
                    }
                }

            }
            bottomSheetDialogSort.dismiss()
        }

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
//////////////////////////////////////////////////////////////////





  /////////////////////////////////////////////////////////////////////////////

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
            searchParams= SearchParams(act.searchId,filterBool,1,1,ratingFrom,ratingTo,viewsFrom,viewsTo)
            requestToBack(searchParams!!)
        }else
            if(act.searchQuery!=null && act.searchQuery.trim()!="")
            {
                searchBar.setText(act.searchQuery,TextView.BufferType.EDITABLE)
                searchParams= SearchParams(act.searchQuery,filterBool,1,1,ratingFrom,ratingTo,viewsFrom,viewsTo)
                requestToBack(searchParams!!)
            }
    }


}
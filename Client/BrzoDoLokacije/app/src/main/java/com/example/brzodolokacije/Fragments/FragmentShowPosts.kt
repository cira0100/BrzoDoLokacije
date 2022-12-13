package com.example.brzodolokacije.Fragments

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
import com.example.brzodolokacije.Models.FilterSort
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
import java.util.Date


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
    private var searchParams:SearchParams?= SearchParams("-1",1,1)
    private lateinit var btnFilter:ImageButton
    private lateinit var btnSort:ImageButton
    private lateinit var searchBar: AutoCompleteTextView
    var responseLocations:MutableList<com.example.brzodolokacije.Models.Location>?=null
    var selectedLocation:com.example.brzodolokacije.Models.Location?=null
    private lateinit var obj:FilterSort

    private lateinit var filter:Button
    private lateinit var removeFilter:Button
    private lateinit var sort:Button

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
        //filter sort validacija


        //filter dialog
        var bottomSheetDialogFilter: BottomSheetDialog
        bottomSheetDialogFilter = BottomSheetDialog(requireContext())
        bottomSheetDialogFilter.setContentView(R.layout.bottom_sheet_filter)

        //sort dialog
        var bottomSheetDialogSort: BottomSheetDialog
        bottomSheetDialogSort = BottomSheetDialog(requireContext())
        bottomSheetDialogSort.setContentView(R.layout.bottom_sheet_sort)




        btnFilter=rootView.findViewById(R.id.btnSortType)
        btnSort=rootView.findViewById(R.id.btnSortDirection)

        var dateFrom=bottomSheetDialogFilter.findViewById<View>(R.id.filterDateFrom)as EditText
        var dateFromDate:Date
        var dateTo=bottomSheetDialogFilter.findViewById<View>(R.id.filterDateTo) as EditText
        var dateToDate:Date
        var ratingFrom=bottomSheetDialogFilter.findViewById<View>(R.id.filterRatingFrom) as EditText
        var ratingTo=bottomSheetDialogFilter.findViewById<View>(R.id.filterRatingTo) as EditText
        var viewsFrom=bottomSheetDialogFilter.findViewById<View>(R.id.filterViewsFrom) as EditText
        var viewsTo=bottomSheetDialogFilter.findViewById<View>(R.id.filterViewsTo) as EditText


        var removeFilter = bottomSheetDialogFilter.findViewById<View>(R.id.btnBSFFilterRemove) as Button

        obj=FilterSort(false,false,0,5,0,1000000000,false,false,false,false)
        obj.filter=false
        obj.sort=false


        btnFilter.setOnClickListener{
            bottomSheetDialogFilter.show()
            var filter = bottomSheetDialogFilter.findViewById<View>(R.id.btnBSFFilter) as Button

            filter.setOnClickListener {
                //validacija unosa
                Toast.makeText(activity, "Method called From Fragment", Toast.LENGTH_LONG).show();
                if(!dateFrom.text.equals("")){
                  obj.filter=true                }
                else if(!dateTo.text.equals("")){
                    obj.filter=true
                }
                if(ratingFrom.text.toString().trim().toInt()>=0){
                    obj.filter=true
                    obj.filterRatingFrom=ratingFrom.text.toString().toInt()

                }
                else{
                    ///toast
                }
                if(ratingTo.text.toString().trim().toInt()>=0){
                    obj.filter=true
                    obj.filterRatingTo=ratingTo.text.toString().toInt()
                }
                else{
                    //toast
                }
                if(viewsFrom.text.toString().trim().toInt()>=0){
                    obj.filter=true
                    obj.filterViewsFrom=viewsFrom.text.toString().toInt()
                }
                else{
                    //toast
                }
                if(viewsTo.text.toString().trim().toInt()>=0){
                    obj.filter=true
                    obj.filterViewsTo=viewsTo.text.toString().trim().toInt()
                }
                else{
                    //toast
                }


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
                        obj.sort = true
                        obj.sortLatest = true
                    } else if (string.equals("Najstarije")) {
                        obj.sort = true
                        obj.sortOldest = true
                    } else if (string.equals("Najbolje ocenjeno")) {
                        obj.sort = true
                        obj.sortBest = true
                    } else if (string.equals("Najviše pregleda")) {
                        obj.sort = true
                        obj.sortMostViews = true
                    } else {
                        obj.sort = false
                    }

                } else {
                    textView.text = "Nothing selected from the radio group"
                }

            }
        }

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

/*
    private fun showBottomSheetFilter() {
        var bottomSheetDialog: BottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter)
        bottomSheetDialog.show()

        var dateFrom=bottomSheetDialog.findViewById<View>(R.id.filterDateFrom)as EditText
        var dateTo=bottomSheetDialog.findViewById<View>(R.id.filterDateTo) as EditText
        var ratingFrom=bottomSheetDialog.findViewById<View>(R.id.filterRatingFrom) as EditText
        var ratingTo=bottomSheetDialog.findViewById<View>(R.id.filterRatingTo) as EditText
        var viewsFrom=bottomSheetDialog.findViewById<View>(R.id.filterViewsFrom) as EditText
        var viewsTo=bottomSheetDialog.findViewById<View>(R.id.filterViewsTo) as EditText
        var filter = bottomSheetDialog.findViewById<View>(R.id.btnBSFFilter) as Button
        var removeFilter = bottomSheetDialog.findViewById<View>(R.id.btnBSFFilterRemove) as Button


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

 */
}
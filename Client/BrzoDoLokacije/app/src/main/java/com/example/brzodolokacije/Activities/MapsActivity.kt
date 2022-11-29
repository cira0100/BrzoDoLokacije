package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.GeocoderHelper
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity() {
    var map: MapView? = null
    var mLocationOverlay: MyLocationNewOverlay?=null
    var mRotationGestureOverlay: RotationGestureOverlay?=null
    var mScaleBarOverlay: ScaleBarOverlay?=null
    var mCompassOverlay: CompassOverlay?=null
    private lateinit var locationManager: LocationManager
    private lateinit var searchButton: MaterialButton
    private lateinit var gpsButton: FloatingActionButton
    private lateinit var confirmButton: FloatingActionButton
    private lateinit var searchBar: AutoCompleteTextView
    var client: FusedLocationProviderClient? = null
    var locLongitude:Double?=null
    var locLatitude:Double?=null
    var selectedLocation:com.example.brzodolokacije.Models.Location?=null
    var responseLocations:MutableList<com.example.brzodolokacije.Models.Location>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val ctx: Context = this
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map= findViewById<MapView>(R.id.ActivityMapsMapView)
        map!!.setTileSource(TileSourceFactory.MAPNIK);
        setUpMap()
        searchButton=findViewById<View>(R.id.ActivityMapsSearchButton) as MaterialButton
        gpsButton=findViewById<View>(R.id.ActivityMapsMyLocation) as FloatingActionButton
        confirmButton=findViewById<View>(R.id.ActivityMapsConfirmLocation) as FloatingActionButton
        searchBar=findViewById<View>(R.id.ActivityMapsSearchBar) as AutoCompleteTextView
        client= LocationServices.getFusedLocationProviderClient(this)
        searchButton.setOnClickListener{
            searchMap()

        }
        gpsButton.setOnClickListener{
            getLocation()
        }
        confirmButton.setOnClickListener{
            if(selectedLocation!=null)
                returnValue()
            else{
                addLocation()

            }
        }
        searchBar.setOnKeyListener(View.OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                searchMap()
                return@OnKeyListener true
            }
            false
        })
        searchBar.addTextChangedListener{
            onTextEnter()
        }
        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("search")
            Log.d("Main",value!!)
            searchBar.setText(value,TextView.BufferType.EDITABLE)
            searchMap()
        }
        setUpSpinner()



    }
    fun addLocation(){
        var editText=EditText(this)
        var dialog=AlertDialog.Builder(this).setTitle("Naziv").setMessage("Unesite naziv")
            .setView(editText)
        dialog.setPositiveButton("Dodaj") { dialog, which ->
            uploadLocation(editText.text.toString())
        }
        dialog.setNegativeButton("Prekini") { dialog, which ->

        }
        dialog.show()

    }
    fun uploadLocation(locationName:String){
        val api =RetrofitHelper.getInstance()
        var geocoder=GeocoderHelper.getInstance()
        var loc1=geocoder!!.getFromLocation(locLatitude!!,locLongitude!!,1)
        if(loc1==null ||loc1.size<=0)
        {
            return
        }
        var countryName=loc1[0].countryName
        var address="todo not possible in query"
        var city="its null"
        if(loc1[0].adminArea!=null)
            city=loc1[0].adminArea//not possible
        var loc: com.example.brzodolokacije.Models.Location =
            com.example.brzodolokacije.Models.Location(
                "",
                locationName,
                city,
                countryName,
                address,
                locLatitude!!,
                locLongitude!!,
                LocationType.GRAD
            )
        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var data=api.addLocation("Bearer "+jwtString,loc)

        data.enqueue(object : retrofit2.Callback<com.example.brzodolokacije.Models.Location?> {
            override fun onResponse(call: Call<com.example.brzodolokacije.Models.Location?>, response: Response<com.example.brzodolokacije.Models.Location?>) {
                if(response.isSuccessful()){
                    selectedLocation=response.body()
                    returnValue()

                }else {

                    if (response.errorBody() != null) {
                        Log.d("Main",response.errorBody()!!.string())
                        Log.d("Main",response.message())
                    }
                    Log.d("Main",response.errorBody()!!.string())
                    Log.d("Main",response.message())
                }


            }

            override fun onFailure(call: Call<com.example.brzodolokacije.Models.Location?>, t: Throwable) {
                Toast.makeText(
                    applicationContext, t.toString(), Toast.LENGTH_LONG
                ).show();
                Log.d("Main",t.toString())
            }
        })
    }
    var arraySpinner :MutableList<String>?=null
    var spinnerAdapter: ArrayAdapter<String>?=null

    fun setUpSpinner() {
        arraySpinner=mutableListOf<String>()
        spinnerAdapter= ArrayAdapter<String>(
        this,
        android.R.layout.simple_list_item_1, arraySpinner!!)
        searchBar.threshold=1
        searchBar.setAdapter(spinnerAdapter)
        searchBar.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            selectedLocation=responseLocations!!.find { location -> location.name==selected }
            Log.d("main",selectedLocation.toString())
            confirmButton.visibility=View.VISIBLE
            val locGeopoint=GeoPoint(selectedLocation!!.latitude,selectedLocation!!.longitude)
            map!!.controller.animateTo(locGeopoint)
            val marker = ContextCompat.getDrawable(this@MapsActivity, R.drawable.ic_baseline_add_location_24);
            locLatitude=selectedLocation!!.latitude
            locLongitude=selectedLocation!!.longitude


            val overlayArray = ArrayList<OverlayItem>()
            val mapItem = OverlayItem(
                "", "", locGeopoint
            )
            mapItem.setMarker(marker)
            overlayArray.add(mapItem)
            if (anotherItemizedIconOverlay == null) {
                anotherItemizedIconOverlay =
                    ItemizedIconOverlay(applicationContext, overlayArray, null)
                map!!.overlays.add(anotherItemizedIconOverlay)
                map!!.invalidate()
            } else {
                map!!.overlays.remove(anotherItemizedIconOverlay)
                map!!.invalidate()
                anotherItemizedIconOverlay =
                    ItemizedIconOverlay(applicationContext, overlayArray, null)
                map!!.overlays.add(anotherItemizedIconOverlay)
            }

        })


    }
    var anotherItemizedIconOverlay: ItemizedIconOverlay<OverlayItem>? = null
    fun returnValue(){
        val intent = intent
        val bundle = Bundle()
        if(selectedLocation==null)
            return
        if(selectedLocation!!.latitude!=locLatitude || selectedLocation!!.longitude!=locLongitude)
        {
            addLocation()
            return
        }
        bundle.putString("locationId",selectedLocation!!._id)
        bundle.putString("name",selectedLocation!!.name)
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }
    fun onTextEnter(){
        var api=RetrofitHelper.getInstance()
        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var text=searchBar.text
        if(text==null ||text.toString().trim()=="")
            return
        var data=api.searchLocationsQuery("Bearer "+jwtString,text.toString())
        data.enqueue(object : retrofit2.Callback<MutableList<com.example.brzodolokacije.Models.Location>> {
            override fun onResponse(call: Call<MutableList<com.example.brzodolokacije.Models.Location>?>, response: Response<MutableList<com.example.brzodolokacije.Models.Location>>) {
                if(response.isSuccessful){
                    var existingLocation=responseLocations
                    responseLocations=response.body()!!
                    var tempList=mutableListOf<String>()
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

            override fun onFailure(call: Call<MutableList<com.example.brzodolokacije.Models.Location>>, t: Throwable) {

            }
        })


    }
    override fun onResume() {
        super.onResume()
        map!!.onResume()
    }
    override fun onPause() {
        super.onPause()
        map!!.onPause()
    }
    fun setUpMap(){
        //Set up controlls and startPoint
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);
        val mapController = map!!.controller
        mapController.setZoom(15)
        fixNetworkPolicy()

        //my location
        //checkLocPerm()
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        mLocationOverlay!!.enableMyLocation()
        map!!.getOverlays().add(this.mLocationOverlay)

        //start point
        val startPoint = GeoPoint(44.0107,20.9181)//dodati nasu lokaciju TODO
        mapController.setCenter(startPoint)

        //rotation gestures
        mRotationGestureOverlay = RotationGestureOverlay(this, map)
        mRotationGestureOverlay!!.setEnabled(true)
        map!!.setMultiTouchControls(true)
        map!!.getOverlays().add(this.mRotationGestureOverlay)
        //Map scale bar
        val dm: DisplayMetrics =this.resources.displayMetrics
        mScaleBarOverlay = ScaleBarOverlay(map);
        mScaleBarOverlay!!.setCentred(true);
        mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10);
        map!!.getOverlays().add(this.mScaleBarOverlay);

        //Compass
        this.mCompassOverlay =
            CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        mCompassOverlay!!.enableCompass()
        map!!.getOverlays().add(this.mCompassOverlay)


        val touchOverlay: Overlay = object : Overlay(this) {

            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                val marker = ContextCompat.getDrawable(this@MapsActivity, R.drawable.ic_baseline_add_location_24);
                val proj: Projection = mapView.projection
                val loc = proj.fromPixels(e.x.toInt(), e.y.toInt())
                val longitude = java.lang.Double.toString(loc.longitudeE6.toDouble() / 1000000)
                val latitude = java.lang.Double.toString(loc.latitudeE6.toDouble() / 1000000)
                locLongitude=loc.longitude
                locLatitude=loc.latitude
                Log.d("main"," "+locLongitude)
                Log.d("main"," "+locLatitude)
                val overlayArray = ArrayList<OverlayItem>()
                val mapItem = OverlayItem(
                    "", "", GeoPoint(
                        loc.latitudeE6.toDouble() / 1000000,
                        loc.longitudeE6.toDouble() / 1000000
                    )
                )
                mapItem.setMarker(marker)
                overlayArray.add(mapItem)
                if (anotherItemizedIconOverlay == null) {
                    anotherItemizedIconOverlay =
                        ItemizedIconOverlay(applicationContext, overlayArray, null)
                    mapView.overlays.add(anotherItemizedIconOverlay)
                    mapView.invalidate()
                } else {
                    mapView.overlays.remove(anotherItemizedIconOverlay)
                    mapView.invalidate()
                    anotherItemizedIconOverlay =
                        ItemizedIconOverlay(applicationContext, overlayArray, null)
                    mapView.overlays.add(anotherItemizedIconOverlay)
                }
                //      dlgThread();
                confirmButton.visibility=View.VISIBLE
                return true
            }
        }
        map!!.getOverlays().add(touchOverlay)
    }
    fun checkLocPerm(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }

    fun fixNetworkPolicy(){
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }
    }
    fun searchMap(){

        var geocoder= GeocoderHelper.getInstance()
        //Log.d("Main",geocoder!!.getFromLocationName("Paris",1)[0].countryName)
        var locString=searchBar.text.toString().trim()
        if(locString==null || locString=="")
            Toast.makeText(this,"Unesite naziv lokacije", Toast.LENGTH_LONG)
        else{
            var temp=geocoder!!.getFromLocationName(locString,1)
            if(temp.size<=0) {
                Toast.makeText(this,"Nepostojeca lokacija",Toast.LENGTH_LONG)
                return
            }
            var result=temp[0]
            if(result==null)
                Toast.makeText(this,"Nepostojeca lokacija", Toast.LENGTH_LONG)
            else{
                //Move to spot
                val searchPoint = GeoPoint(result.latitude,result.longitude)
                map!!.controller.animateTo(searchPoint)


            }
        }
    }
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission
                    .ACCESS_FINE_LOCATION)
            == PackageManager
                .PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission
                    .ACCESS_COARSE_LOCATION)
            == PackageManager
                .PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            getCurrentLocation()
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    111
                )
            }

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        // Check condition
        if (requestCode == 111 && grantResults.size > 0
            && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            // When permission are granted
            // Call  method
            getCurrentLocation()
        } else {
            // When permission are denied
            // Display toast
            Toast
                .makeText(
                    this,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }
    @Suppress("MissingPermission")
    private fun getCurrentLocation() {
        val locationManager = this
            .getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
        checkLocPerm()
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
            || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {

            client!!.getLastLocation().addOnCompleteListener { task ->
                var location = task.result
                if (location == null) {
                    val locationRequest: LocationRequest = LocationRequest()
                        .setPriority(
                            LocationRequest.PRIORITY_HIGH_ACCURACY
                        )
                        .setInterval(10000)
                        .setFastestInterval(
                            1000
                        )
                        .setNumUpdates(1)

                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(
                            locationResult: LocationResult
                        ) {
                            // Initialize
                            // location
                            val location1: Location? = locationResult
                                .lastLocation
                            // Set latitude
                            map!!.controller.animateTo(
                                GeoPoint(
                                    location1!!.latitude,
                                    location1!!.longitude
                                )
                            )

                        }
                    }
                    client!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    );
                } else {
                    map!!.controller.animateTo(GeoPoint(location!!.latitude, location!!.longitude))
                }


            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
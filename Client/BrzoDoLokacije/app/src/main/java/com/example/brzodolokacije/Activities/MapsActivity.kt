package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.GeocoderHelper
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


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
    private lateinit var searchBar: TextInputEditText
    var client: FusedLocationProviderClient? = null
    var locLongitude:Double?=null
    var locLatitude:Double?=null
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
        searchBar=findViewById<View>(R.id.ActivityMapsSearchBar) as TextInputEditText
        client= LocationServices.getFusedLocationProviderClient(this)
        searchButton.setOnClickListener{
            searchMap()

        }
        gpsButton.setOnClickListener{
            getLocation()
        }
        confirmButton.setOnClickListener{
            if(locLatitude!=null && locLatitude!=null)
                returnValue()
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
        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("search")
            Log.d("Main",value!!)
            searchBar.setText(value,TextView.BufferType.EDITABLE)
            searchMap()
        }



    }
    fun returnValue(){
        val intent = intent
        val bundle = Bundle()
        bundle.putDouble("longitude", locLongitude!!)
        bundle.putDouble("latitude", locLatitude!!)
        if(searchBar.text!=null && !searchBar.text.toString().equals(""))
            bundle.putString("name", searchBar.text.toString())
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
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
            var anotherItemizedIconOverlay: ItemizedIconOverlay<OverlayItem>? = null
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
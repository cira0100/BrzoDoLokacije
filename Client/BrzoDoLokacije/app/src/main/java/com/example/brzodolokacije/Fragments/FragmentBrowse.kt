package com.example.brzodolokacije.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class FragmentBrowse : Fragment(R.layout.fragment_browse) {

    var map: MapView? = null
    var mLocationOverlay:MyLocationNewOverlay?=null
    var mRotationGestureOverlay:RotationGestureOverlay?=null
    var mScaleBarOverlay: ScaleBarOverlay?=null
    var mCompassOverlay:CompassOverlay?=null
    private lateinit var locationManager: LocationManager
    private lateinit var searchButton: MaterialButton
    private lateinit var gpsButton:FloatingActionButton
    private lateinit var searchBar: TextInputEditText
    var client: FusedLocationProviderClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v:View=inflater.inflate(R.layout.fragment_browse, container, false)
        val ctx: Context = requireContext()
        Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx));
        map=v.findViewById(R.id.FragmentBrowseMapView) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK);
        setUpMap()
        searchButton=v.findViewById<View>(R.id.FragmentBrowseSearchButton) as MaterialButton
        gpsButton=v.findViewById<View>(R.id.FragmentBrowseMyLocation) as FloatingActionButton
        searchBar=v.findViewById<View>(R.id.FragmentBrowseSearchBar) as TextInputEditText
        client=LocationServices.getFusedLocationProviderClient(requireActivity())
        searchButton.setOnClickListener{
            searchMap()

        }
        gpsButton.setOnClickListener{
            getLocation()
        }
        searchBar.setOnKeyListener(OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                searchMap()
                return@OnKeyListener true
            }
            false
        })

        return v
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
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        mLocationOverlay!!.enableMyLocation()
        map!!.getOverlays().add(this.mLocationOverlay)

        //start point
        val startPoint = GeoPoint(44.0107,20.9181)//dodati nasu lokaciju TODO
        mapController.setCenter(startPoint)

        //rotation gestures
        mRotationGestureOverlay = RotationGestureOverlay(context, map)
        mRotationGestureOverlay!!.setEnabled(true)
        map!!.setMultiTouchControls(true)
        map!!.getOverlays().add(this.mRotationGestureOverlay)
        //Map scale bar
        val dm:DisplayMetrics=requireContext().resources.displayMetrics
        mScaleBarOverlay = ScaleBarOverlay(map);
        mScaleBarOverlay!!.setCentred(true);
        mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10);
        map!!.getOverlays().add(this.mScaleBarOverlay);

        //Compass
        this.mCompassOverlay =
            CompassOverlay(context, InternalCompassOrientationProvider(context), map)
        mCompassOverlay!!.enableCompass()
        map!!.getOverlays().add(this.mCompassOverlay)
    }
    fun checkLocPerm(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }

    fun fixNetworkPolicy(){
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
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
            Toast.makeText(requireContext(),"Unesite naziv lokacije",Toast.LENGTH_SHORT)
        else{
            var result=geocoder!!.getFromLocationName(locString,1)[0]
            if(result==null)
                Toast.makeText(requireContext(),"Nepostojeca lokacija",Toast.LENGTH_SHORT)
            else{
                //move to spot
                map!!.controller.animateTo(GeoPoint(result.latitude,result.longitude))


            }
        }
    }
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission
                    .ACCESS_FINE_LOCATION)
            == PackageManager
                .PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission
                    .ACCESS_COARSE_LOCATION)
            == PackageManager
                .PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            getCurrentLocation()
        }
        else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                111
            )

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
                    activity,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }
    @Suppress("MissingPermission")
    private fun getCurrentLocation(){
        val locationManager = requireActivity()
            .getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
        checkLocPerm()
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)){

            client!!.getLastLocation().addOnCompleteListener {task ->
                var location = task.result
                if(location == null) {
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
                            map!!.controller.animateTo(GeoPoint(location1!!.latitude,location1!!.longitude))
                            Toast.makeText(requireContext()," "+location1!!.latitude,Toast.LENGTH_LONG)

                        }
                    }
                    client!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper());
                } else {
                    map!!.controller.animateTo(GeoPoint(location!!.latitude,location!!.longitude))
                    Toast.makeText(requireContext()," "+location.latitude,Toast.LENGTH_LONG)
                }



        }
    }




}
}
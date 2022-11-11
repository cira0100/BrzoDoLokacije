package com.example.brzodolokacije.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.R
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


        //my location
        //checkLocPerm()
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        mLocationOverlay!!.enableMyLocation()
        map!!.getOverlays().add(this.mLocationOverlay)
        //var res=Geocoder(requireContext()).getFromLocationName("Paris",1)
        //Log.d("Main",res.toString())
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


}
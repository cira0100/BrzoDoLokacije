package com.example.brzodolokacije.Fragments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class FragmentBrowse : Fragment(R.layout.fragment_browse) {

    var map: MapView? = null
    var mLocationOverlay:MyLocationNewOverlay?=null
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
        val startPoint = GeoPoint(44.0073, 20.9227)
        mapController.setCenter(startPoint)
        
    }


}
package com.example.brzodolokacije.Fragments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.R
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration;

class FragmentBrowse : Fragment(R.layout.fragment_browse) {

    var map: MapView? = null

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





        return v
    }

}
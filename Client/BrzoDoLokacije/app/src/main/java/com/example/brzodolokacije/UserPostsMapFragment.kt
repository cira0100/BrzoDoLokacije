package com.example.brzodolokacije

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Fragments.FragmentProfile
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Response


class UserPostsMapFragment : Fragment() {

    var map: MapView? = null
    var id:String?=null
    var backButton:ImageView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_user_posts_map, container, false)
        val ctx: Context = requireContext()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map=view.findViewById(R.id.FragmentUserPostsMapMapView) as MapView
        backButton=view.findViewById(R.id.btnFragmentUserPostsBack) as ImageView
        map!!.setTileSource(TileSourceFactory.MAPNIK);
        id=this.requireArguments().getString("id");//https://stackoverflow.com/questions/17436298/how-to-pass-a-variable-from-activity-to-fragment-and-pass-it-back
        setUpMap()
        backButton!!.setOnClickListener{
            //SUBJECT TO CHANGE
            val fragmentProfile = FragmentProfile()
            fragmentManager?.beginTransaction()
                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentProfile)
                ?.commit()
//How to call fragment
//            val bundle = Bundle()
//            bundle.putString("id",userId )
//            val fragmentFollowers = UserPostsMapFragment()
//            fragmentFollowers.setArguments(bundle)
//
//            fragmentManager
//                ?.beginTransaction()
//                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentFollowers)
//                ?.commit()
        }
        return  view
    }
    fun setUpMap(){
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);
        val mapController = map!!.controller
        mapController.setZoom(15)
        var api= RetrofitHelper.getInstance()
        var jwtString= SharedPreferencesHelper.getValue("jwt",requireActivity())
        if(id==null)
            return
        var data=api.getUsersPosts("Bearer "+jwtString,id!!)

        data.enqueue(object : retrofit2.Callback<MutableList<PostPreview>> {
            override fun onResponse(call: Call<MutableList<PostPreview>>, response: Response<MutableList<PostPreview>>) {
                if(response.isSuccessful()){
                    var postList=response.body()
                    if (postList != null) {
                        var flag=true
                        for(post in postList){
                            Log.d("main",post.toString())
                            val startMarker = Marker(map)
                            startMarker.setPosition(GeoPoint(post.location.latitude,post.location.longitude))
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            if(flag){
                                flag=false
                                map!!.controller.animateTo(GeoPoint(post.location.latitude,post.location.longitude))
                            }

                            startMarker.setOnMarkerClickListener(object:
                                Marker.OnMarkerClickListener {
                                override fun onMarkerClick(
                                    marker: Marker?,
                                    mapView: MapView?
                                ): Boolean {
                                    val intent: Intent = Intent(activity, ActivitySinglePost::class.java)
                                    var b=Bundle()
                                    b.putParcelable("selectedPost",post)
                                    intent.putExtras(b)
                                    requireActivity().startActivity(intent)
                                    return true
                                }

                            })
                            map!!.getOverlays().add(startMarker)


                        }
                    }



                }else {

                }


            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
            }
        })
    }


}
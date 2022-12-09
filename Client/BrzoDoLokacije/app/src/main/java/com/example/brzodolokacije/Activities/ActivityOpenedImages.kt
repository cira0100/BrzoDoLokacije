package com.example.brzodolokacije.Activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.brzodolokacije.Adapters.OpenedPostImageAdapter
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.databinding.ActivityOpenedImagesBinding

class ActivityOpenedImages : AppCompatActivity() {
    lateinit var binding:ActivityOpenedImagesBinding
    var rvImages: RecyclerView?=null
    var linearLayout:LinearLayoutManager?=null
    var adapter:OpenedPostImageAdapter?=null
    var images:List<PostImage>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenedImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            images=(intent.extras?.getParcelable("post",PostPreview::class.java) as PostPreview).images
        }
        else{
            images=(intent.extras?.getParcelable("post") as PostPreview?)?.images
        }

        setRecyclerView()
    }

    fun setListeners(){

    }

    fun setRecyclerView(){
        rvImages=binding.rvImages
        linearLayout= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapter= OpenedPostImageAdapter(images,this)
        rvImages!!.setHasFixedSize(true)
        var snap:SnapHelper=PagerSnapHelper()
        snap.attachToRecyclerView(rvImages)
        rvImages!!.layoutManager=linearLayout
        rvImages!!.adapter=adapter
    }

}
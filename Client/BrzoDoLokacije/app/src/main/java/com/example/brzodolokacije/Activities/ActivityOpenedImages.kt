package com.example.brzodolokacije.Activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.brzodolokacije.Adapters.OpenedPostImageAdapter
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.databinding.ActivityOpenedImagesBinding
import java.util.*

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
        setListeners()
    }

    fun setListeners(){
        binding.btnBackToPost.setOnClickListener {
            finish()
        }
        binding.btnDownload.setOnClickListener {
            //uzmi id trenutne slike
            var selected:PostImage?=null
            linearLayout?.findFirstVisibleItemPosition()?.let { it1 -> selected=images?.get(it1) }
            if(selected!=null){
                //sacuvaj na telefonu
                var image=Glide.with(this)
                    .asBitmap()
                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + selected!!._id)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Toast.makeText(this@ActivityOpenedImages,"Slika se preuzima...",Toast.LENGTH_LONG).show()
                            MediaStore.Images.Media.insertImage(contentResolver, resource, "odyssey_"+ Calendar.getInstance().timeInMillis , "");
                            Toast.makeText(this@ActivityOpenedImages,"Slika je sačuvana.",Toast.LENGTH_LONG).show()
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        }
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
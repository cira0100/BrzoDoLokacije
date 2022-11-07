package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.R
import java.net.URI


class ActivityAddPost : AppCompatActivity() {
    private lateinit var uploadFromGallery: Button
    private lateinit var takePhoto: Button
    private lateinit var showNextImage:Button
    private lateinit var showPreviousImage:Button
    private lateinit var switcher: ImageSwitcher
    private var uploadedImages:ArrayList<Uri?>?=null
    //private var paths :ArrayList<String?>?=null
    private var place=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
//        Toast.makeText(
//            applicationContext, "Add new ", Toast.LENGTH_LONG
//        ).show();
        uploadedImages= ArrayList()

        //paths= ArrayList()

        uploadFromGallery=findViewById<View>(R.id.btnActivityAddPostUploadImages) as Button
        takePhoto=findViewById<View>(R.id.btnActivityAddPosTakeImage) as Button
        showNextImage=findViewById<View>(R.id.nextImage) as Button
        showPreviousImage=findViewById<View>(R.id.previousImage) as Button
        switcher=findViewById<View>(R.id.switcher) as ImageSwitcher

        switcher?.setFactory{
            val imgView = ImageView(applicationContext)
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            imgView.setPadding(8, 8, 8, 8)
            imgView}

        //dodavanje iz galerije
        uploadFromGallery.setOnClickListener{

            //provera da li je odobrena upotreba galerije
            if(ContextCompat.checkSelfPermission(this@ActivityAddPost, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@ActivityAddPost, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
            }

            //otvaranje galerije
            val intent= Intent(Intent.ACTION_PICK)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Izaberi fotografije"),0)
        }
/*
        //fotografisanje
        takePhoto.setOnClickListener {
            //provera da li je odobrena upotreba kamere
            if(ContextCompat.checkSelfPermission(this@ActivityAddPost, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@ActivityAddPost, arrayOf(Manifest.permission.CAMERA),200)
            }
        }

*/
        //prikaz ucitanih
        //
       showPreviousImage.setOnClickListener{
            if(place>0){
                place=place-1
                switcher.setImageURI(uploadedImages!![place])
                showNextImage.isEnabled=true
            }
            else{
                showPreviousImage.isEnabled=false
            }
        }


        showNextImage.setOnClickListener{
            if(place<uploadedImages!!.size-1){
                place=place+1
                switcher.setImageURI(uploadedImages!![place])
                showPreviousImage.isEnabled=true
            }
            else{
                showNextImage.isEnabled=false
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //nakon otvaranja
        if(requestCode==0 && resultCode== RESULT_OK){
            //samo jedna slika
            //image.setImageURI(data?.data)

            //veci broj slika
            if (data!!.getClipData() != null) {
                var count = data!!.clipData!!.itemCount

                for (i in 0..count - 1) {
                    var _uri: Uri = data!!.clipData!!.getItemAt(i).uri
                    uploadedImages!!.add(_uri)
                }

            // prikaz ucitanih
            switcher.setImageURI(uploadedImages!![0])
            place=0
            //jedna slika
            } else if (data?.getData() != null) {
                uploadedImages!!.add(data.data!!)

                //prikaz jedne ucitane
                switcher.setImageURI(data.data!!)
            }
        }
    }



    /*
    private fun showImportedImages(){
        var cols= listOf<String>(MediaStore.Images.Thumbnails.DATA).toTypedArray()
        rs= contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cols,null,null,null)!!
        imagesGrid.adapter=ImageAdapter(applicationContext)
    }

    inner class ImageAdapter:BaseAdapter{
        lateinit var context: Context

        constructor(contect: Context){
            this.context=context
        }

        override fun getCount(): Int {
            return rs.count
        }

        override fun getItem(p0: Int): Any {
            return p0
        }

        override fun getItemId(p0: Int): Long {
            return p0 as Long
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            //prikaz slike u GridView-u

            //generisanje jednog imageView-a
            var imageView=ImageView(context)
            rs.moveToPosition(p0)
            var path=rs.getString(0)
            var bitmap=BitmapFactory.decodeFile(path)

            imageView.setImageBitmap(bitmap)
            imageView.layoutParams=AbsListView.LayoutParams(300,300)
            return imageView

        }

    }*/

}
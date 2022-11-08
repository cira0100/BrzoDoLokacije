package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.Post
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Models.PostSend
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI


class ActivityAddPost : AppCompatActivity() {
    private lateinit var uploadFromGallery: Button
    private lateinit var takePhoto: Button
    private lateinit var showNextImage:Button
    private lateinit var showPreviousImage:Button
    private lateinit var switcher: ImageSwitcher
    private var uploadedImages:ArrayList<Uri?>?=null

    private lateinit var location:EditText
    private lateinit var description:EditText
    private lateinit var locationString:String
    private lateinit var descriptionString:String
    private lateinit var post:Button
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

        uploadFromGallery=findViewById<View>(R.id.btnActivityAddPostUploadFromGallery) as Button
        showNextImage=findViewById<View>(R.id.nextImage) as Button
        showPreviousImage=findViewById<View>(R.id.previousImage) as Button
        switcher=findViewById<View>(R.id.isActivityAddPostSwitcher) as ImageSwitcher
        location=findViewById<View>(R.id.etActivityAddPostLocation) as EditText
        description=findViewById<View>(R.id.etActivityAddPostDescription) as EditText
        post=findViewById<View>(R.id.btnActivityAddPostPost) as Button


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

        post.setOnClickListener{
            locationString=location.text.toString().trim()
            descriptionString=description.text.toString().trim()
            //prazan unos?
            if(locationString.isEmpty()) {
                location.hint="Unesite lokaciju"
                location.setHintTextColor(Color.RED)
            }
            if(descriptionString.isEmpty()) {
                description.hint="Unesite lokaciju"
                description.setHintTextColor(Color.RED)
            }

            if(!locationString.isEmpty() && !descriptionString.isEmpty()){
                sendPost()
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
private fun sendPost(){
    val api =RetrofitHelper.getInstance()


    var obj=PostSend("","","")

    var loc=locationString
    location.text.clear()
    var desc=descriptionString
    description.text.clear()

    obj.locationId=loc
    obj.description=desc

    //dodavanje u bazu

}
}
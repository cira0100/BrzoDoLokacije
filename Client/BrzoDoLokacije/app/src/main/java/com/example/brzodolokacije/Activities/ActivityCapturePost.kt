package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import java.io.IOException

class ActivityCapturePost : AppCompatActivity() {

    private lateinit var takePhoto: Button
    private lateinit var location: EditText
    private lateinit var description: EditText
    private lateinit var locationString:String
    private lateinit var descriptionString:String
    private lateinit var post:Button
    private lateinit var showImage:ImageView
    private var uploadedImages:ArrayList<Uri?>?=null
    private lateinit var photoPath:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_post)

        //provera da li je odobrena upotreba skladista
        if(ContextCompat.checkSelfPermission(this@ActivityCapturePost, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@ActivityCapturePost, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
        }
        //provera da li je odobrena upotreba kamere
        if(ContextCompat.checkSelfPermission(this@ActivityCapturePost, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@ActivityCapturePost, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
        }

        //provera da li je odobren upis u skladiste
        if(ContextCompat.checkSelfPermission(this@ActivityCapturePost, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@ActivityCapturePost, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
        }

        location=findViewById<View>(R.id.etActivityCapturePostLocation) as EditText
        description=findViewById<View>(R.id.etActivityCapturePostDescription) as EditText
        post=findViewById<View>(R.id.btnActivityCapturePostPost) as Button
        showImage=findViewById<View>(R.id.ivActivityCapturePostImage) as ImageView
        takePhoto=findViewById<View>(R.id.btnActivityCapturePostCapture) as Button

        //dodavanje sa kamere
        takePhoto.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            Toast.makeText(
               applicationContext, "take photo is working", Toast.LENGTH_LONG
            ).show();
            if(cameraIntent.resolveActivity(packageManager)!=null){
                var photoFile: File?=null
                try {
                    Toast.makeText(
                        applicationContext, "try", Toast.LENGTH_LONG
                    ).show();
                    val fileName="IMG"
                    val destStorageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val photo=File.createTempFile(fileName,".jpg",destStorageDir)
                    photoPath=photo.absolutePath
                    photoFile=photo
                    Toast.makeText(
                        applicationContext, "photoFile generisano", Toast.LENGTH_LONG
                    ).show();
                }catch (e:IOException){Toast.makeText(
                    applicationContext, "greska", Toast.LENGTH_LONG
                ).show();}

                if(photoFile!=null){
                    val _uri=FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,_uri)
                    startActivityForResult(cameraIntent,1)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null){
            Toast.makeText(
                applicationContext, "camera intent", Toast.LENGTH_LONG
            ).show();

            showImage.setImageURI(Uri.parse(photoPath))


        /*var photo:Bitmap=data.extras!!.get("data") as Bitmap
            showImage.setImageBitmap(photo)*/
        }
    }
}
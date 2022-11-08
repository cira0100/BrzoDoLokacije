package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityCapturePost : AppCompatActivity() {
    lateinit var currentPhotoPath: String
    private lateinit var takePhoto: Button
    private lateinit var location: EditText
    private lateinit var description: EditText
    private lateinit var locationString:String
    private lateinit var descriptionString:String
    private lateinit var post:Button
    private lateinit var showImage:ImageView
    private var uploadedImages:ArrayList<Uri?>?=null
    private lateinit var photoPath:String
    private lateinit var photoURI:Uri

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

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
            ActivityCompat.requestPermissions(this@ActivityCapturePost, arrayOf(Manifest.permission.CAMERA),101)
        }

        //provera da li je odobren upis u skladiste
        if(ContextCompat.checkSelfPermission(this@ActivityCapturePost, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@ActivityCapturePost, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),101)
        }

        location=findViewById<View>(R.id.etActivityCapturePostLocation) as EditText
        description=findViewById<View>(R.id.etActivityCapturePostDescription) as EditText
        post=findViewById<View>(R.id.btnActivityCapturePostPost) as Button
        showImage=findViewById<View>(R.id.ivActivityCapturePostImage) as ImageView
        takePhoto=findViewById<View>(R.id.btnActivityCapturePostCapture) as Button

        //dodavanje sa kamere
        takePhoto.setOnClickListener {
            Toast.makeText(
                applicationContext, "camera intent button", Toast.LENGTH_LONG
            ).show();
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    photoFile?.also {
                        photoURI= FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 123)
                    }
                }
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

            /*if(!locationString.isEmpty() && !descriptionString.isEmpty()){

                //dodaj u bazu

            }*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 123 && data != null){
            Toast.makeText(
                applicationContext, "camera intent", Toast.LENGTH_LONG
            ).show();

          showImage.setImageURI(photoURI)

            Toast.makeText(
                applicationContext, currentPhotoPath, Toast.LENGTH_LONG
            ).show();
            /*var photo:Bitmap=data.extras!!.get("data") as Bitmap
                showImage.setImageBitmap(photo)*/
        }
    }


}
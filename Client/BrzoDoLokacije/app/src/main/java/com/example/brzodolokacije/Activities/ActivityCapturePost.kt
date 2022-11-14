package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.brzodolokacije.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ActivityCapturePost : AppCompatActivity() {

    private lateinit var takePhoto: Button
    private lateinit var location: EditText
    private lateinit var description: EditText
    private lateinit var locationString: String
    private lateinit var descriptionString: String
    private lateinit var post: Button
    private lateinit var showImage: ImageView
    private var uploadedImages: ArrayList<Uri?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_post)

        location = findViewById<View>(R.id.etActivityCapturePostLocation) as EditText
        description = findViewById<View>(R.id.etActivityCapturePostDescription) as EditText
        post = findViewById<View>(R.id.btnActivityCapturePostPost) as Button
        showImage = findViewById<View>(R.id.ivActivityCapturePostImage) as ImageView
        takePhoto = findViewById<View>(R.id.btnActivityCapturePostCaptureVisible) as Button

        //dodavanje sa kamere

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //provera da li je odobrena upotreba skladista
        if (ContextCompat.checkSelfPermission(
                this@ActivityCapturePost,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityCapturePost,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        }
        //provera da li je odobrena upotreba kamere
        if (ContextCompat.checkSelfPermission(
                this@ActivityCapturePost,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityCapturePost,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }

        //provera da li je odobren upis u skladiste
        if (ContextCompat.checkSelfPermission(
                this@ActivityCapturePost,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityCapturePost,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        takePhoto.setOnClickListener {

            val APP_TAG = "BrzoDoLokacije"
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
 /*           val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            //val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            //val photo= File(storageDir,"JPEG_${timeStamp}.jpg")

            val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory")
            }
            var photoFile = File(mediaStorageDir.path + File.separator + "${APP_TAG}_${timeStamp}.jpg")

            if (photoFile != null) {
                val fileProvider: Uri =
                    FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            }

*/
            cameraActivityResultLauncher.launch(takePictureIntent)
        }

        post.setOnClickListener {
            locationString = location.text.toString().trim()
            descriptionString = description.text.toString().trim()
            //prazan unos?
            if (locationString.isEmpty()) {
                location.hint = "Unesite lokaciju"
                location.setHintTextColor(Color.RED)
            }
            if (descriptionString.isEmpty()) {
                description.hint = "Unesite lokaciju"
                description.setHintTextColor(Color.RED)
            }



        }
    }
        private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === RESULT_OK && result.data != null) {
                val bundle = result.data!!.extras
                val bitmap = bundle!!["data"] as Bitmap?
                showImage.setImageBitmap(bitmap)
                takePhoto.isVisible=false

            }

        }
    }







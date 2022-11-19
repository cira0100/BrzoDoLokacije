package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.brzodolokacije.Models.Location
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.GeocoderHelper
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class ActivityCapturePost : AppCompatActivity() {

    private lateinit var takePhoto: Button
    private lateinit var location: EditText
    private lateinit var description: EditText
    private lateinit var locationString: String
    private lateinit var descriptionString: String
    private lateinit var post: Button
    private lateinit var showImage: ImageView
    private var uploadedImages: Uri? = null
    private lateinit var addLocation:Button

    val incorectCoord:Double=1000.0
    val LOCATIONREQCODE=123
    var longitude:Double=incorectCoord
    var latitude:Double=incorectCoord
    var progressDialog: ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_post)
        location = findViewById<View>(R.id.etActivityCapturePostLocation) as EditText
        description = findViewById<View>(R.id.etActivityCapturePostDescription) as EditText
        post = findViewById<View>(R.id.btnActivityCapturePostPost) as Button
        showImage = findViewById<View>(R.id.ivActivityCapturePostImage) as ImageView
        takePhoto = findViewById<View>(R.id.btnActivityCapturePostCaptureVisible) as Button
        addLocation=findViewById<View>(R.id.btnActivityCapturePostAddLocation) as Button

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Molimo sacekajte!!!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)


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


        addLocation.setOnClickListener {
            val myIntent = Intent(this, MapsActivity::class.java)
            startActivityForResult(myIntent,LOCATIONREQCODE)
        }

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
            }else
            if (descriptionString.isEmpty()) {
                description.hint = "Unesite opis"
                description.setHintTextColor(Color.RED)
            }else if(f!=null && longitude!=incorectCoord && latitude!=incorectCoord){
                    uploadLocation()
            }




        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==LOCATIONREQCODE && resultCode== RESULT_OK){
            var bundle=data!!.extras
            longitude=bundle!!.getDouble("longitude",incorectCoord)
            latitude=bundle!!.getDouble("latitude",incorectCoord)
        }
    }
    var f:File?=null
        private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === RESULT_OK && result.data != null) {
                val bundle = result.data!!.extras
                val bitmap = bundle!!["data"] as Bitmap?
                val outputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                val bitmapdata = outputStream.toByteArray()
                val inputstream: InputStream = ByteArrayInputStream(bitmapdata)
                f=File.createTempFile("temp","12345")
                f!!.writeBytes(inputstream!!.readBytes())



                showImage.setImageBitmap(bitmap)
                takePhoto.isVisible=false

            }

        }
    fun uploadLocation() {
        //TO DO SEARCH EXISTING LOCATION FROM DB
        //IF NOT EXISTS ADD NEW LOCATION
        progressDialog!!.show()
        val api =RetrofitHelper.getInstance()
        var geocoder= GeocoderHelper.getInstance()
        var loc1=geocoder!!.getFromLocation(latitude,longitude,1)
        if(loc1==null ||loc1.size<=0)
        {
            progressDialog!!.dismiss()
            Toast.makeText(this,"Lokacija ne postoji",Toast.LENGTH_LONG);
            return
        }
        var countryName=loc1[0].countryName
        var address="todo not possible in query"
        var city=loc1[0].adminArea//not possible
        var loc:Location=Location("",locationString,city,countryName,address,latitude,longitude,LocationType.GRAD)
        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var data=api.addLocation("Bearer "+jwtString,loc)

        data.enqueue(object : retrofit2.Callback<Location?> {
            override fun onResponse(call: Call<Location?>, response: Response<Location?>) {
                if(response.isSuccessful()){

                    uploadPost(response.body()!!._id)
                    Toast.makeText(
                        applicationContext, "USPEH", Toast.LENGTH_LONG
                    ).show();

                }else {
                    progressDialog!!.dismiss()

                    if (response.errorBody() != null) {
                        Log.d("Main",response.errorBody()!!.string())
                        Log.d("Main",response.message())
                    }
                    Log.d("Main",response.errorBody()!!.string())
                    Log.d("Main",response.message())
                }


            }

            override fun onFailure(call: Call<Location?>, t: Throwable) {
                Toast.makeText(
                    applicationContext, t.toString(), Toast.LENGTH_LONG
                ).show();
                Log.d("Main",t.toString())
                progressDialog!!.dismiss()
            }
        })
    }
    fun uploadPost(loc:String){
        val api = RetrofitHelper.getInstance()
        var desc=descriptionString
        description.text.clear()
        //loc
        //desc
        var locReq= RequestBody.create("text/plain".toMediaTypeOrNull(),loc)
        var descReq= RequestBody.create("text/plain".toMediaTypeOrNull(),desc)
        var idReq= RequestBody.create("text/plain".toMediaTypeOrNull(),"dsa")
        val imagesParts = arrayOfNulls<MultipartBody.Part>(
            1
        )

            var imageBody= RequestBody.create("image/*".toMediaTypeOrNull(),f!!)
            imagesParts[0]= MultipartBody.Part.createFormData("images",f!!.name,imageBody)

        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var data=api.addPost("Bearer "+jwtString,imagesParts,idReq,descReq,locReq)


        data.enqueue(object : retrofit2.Callback<PostPreview?> {
            override fun onResponse(call: Call<PostPreview?>, response: Response<PostPreview?>) {
                if(response.isSuccessful()){
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        applicationContext, "USPEH", Toast.LENGTH_LONG
                    ).show();
                }else {
                    progressDialog!!.dismiss()

                    if (response.errorBody() != null) {
                        Toast.makeText(
                            applicationContext,
                            response.errorBody()!!.string(),
                            Toast.LENGTH_LONG
                        ).show();
                        Log.d("Main",response.errorBody()!!.string())
                    }
                }


            }

            override fun onFailure(call: Call<PostPreview?>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(
                    applicationContext, t.toString(), Toast.LENGTH_LONG
                ).show();
                Log.d("Main",t.toString())
            }
        })
    }
    }







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
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setMargins
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
    private lateinit var description: EditText
    private lateinit var descriptionString: String
    private lateinit var post: Button
    private lateinit var showImage: ImageView
    private lateinit var addLocation:Button
    private lateinit var tagLayout:LinearLayout
    private lateinit var tagButtons:MutableList<Button>
    private lateinit var tagText: EditText
    private lateinit var tagButtonAdd:Button
    private lateinit var tagList: MutableList<String>
    private var tagidcounter:Int = 0
    private lateinit var addDescription:Button
    private lateinit var locText: EditText


    val LOCATIONREQCODE=123
    var locationId:String?=null

    var progressDialog: ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_post)
        tagList = mutableListOf()
        tagButtons= mutableListOf()
        tagidcounter = 0

        description = findViewById<View>(R.id.etActivityCapturePostDescription) as EditText
        post = findViewById<View>(R.id.btnActivityCapturePostPost) as Button
        showImage = findViewById<View>(R.id.ivActivityCapturePostImage) as ImageView
        takePhoto = findViewById<View>(R.id.btnActivityCapturePostCaptureVisible) as Button
        addLocation=findViewById<View>(R.id.btnActivityCapturePostAddLocation) as Button
        tagText =findViewById<View>(R.id.acTagsCap) as EditText
        tagButtonAdd = findViewById<View>(R.id.btnActivityAddPostAddTagCap) as Button
        tagLayout =  findViewById<View>(R.id.llTagsCap) as LinearLayout

        addDescription=findViewById<View>(R.id.tvActivityCapturePostDescriptiontext)as Button
        locText=findViewById<View>(R.id.etActivityAddPostLocationText) as EditText


        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Molimo sacekajte!!!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        tagText.isGone=true
        tagText.isVisible=false
        description.isGone=true
        description.isVisible=false

        addDescription.setOnClickListener {
            description.isGone=false
            description.isVisible=true
        }
        //dodavanje i brisanje tagova
        tagButtonAdd.setOnClickListener {
            addTag()
        }
        tagText.setOnKeyListener(View.OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                addTag()
                return@OnKeyListener true
            }
            false
        })

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
           // if(location.text!=null && !location.text.trim().equals(""))
              //  myIntent.putExtra("search",location.text.toString())
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
           // locationString = location.text.toString().trim()
            descriptionString = description.text.toString().trim()
            //prazan unos?
            if (descriptionString.isEmpty()) {
                description.hint = "Unesite opis"
                description.setHintTextColor(Color.RED)
            }else if(f!=null && locationId!=null && locationId!!.trim()!=""){
                    uploadLocation()
            }




        }
    }
    fun addTag(){
        tagText.isGone=false
        tagText.isVisible=true

        if(tagList.count()<4  && tagText.text.toString().length>=3) {
            var tagstr = tagText.text.toString()
            var newbtn = Button(this)
            newbtn.setId(tagidcounter)
            newbtn.text = tagstr
            var layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
            )
            layoutParams.setMargins(3)
            newbtn.layoutParams=layoutParams
            newbtn.setBackgroundColor(Color.parseColor("#1C789A"))
            newbtn.setTextColor(Color.WHITE)
            newbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            newbtn.setPadding(3,1,3,1)

            newbtn.setOnClickListener {
                var btntext = newbtn.text.toString()
                tagList.remove(btntext)
                tagButtons.remove(newbtn)
                tagLayout.removeView(newbtn)
            }

            tagList.add(tagstr)
            tagButtons.add(newbtn)
            tagLayout.addView(newbtn)
            tagText.text.clear()
        }
        else{
            Toast.makeText(this,"Maksimalno 4 tagova ( duzine + karaktera)",Toast.LENGTH_LONG)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==LOCATIONREQCODE && resultCode== RESULT_OK){
            var bundle=data!!.extras
            locationId=bundle!!.getString("locationId")
            var name=bundle!!.getString("name")
            locText.isGone=false
            locText.isVisible=true
            locText.setText(name,TextView.BufferType.EDITABLE)
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
        if(locationId!=null && locationId!!.trim()!="")
            uploadPost(locationId!!)
    }
    fun uploadPost(loc:String){
        progressDialog!!.show()
        val api = RetrofitHelper.getInstance()
        var desc=descriptionString
        description.text.clear()
        //loc
        //desc
        var locReq= RequestBody.create("text/plain".toMediaTypeOrNull(),loc)
        var descReq= RequestBody.create("text/plain".toMediaTypeOrNull(),desc)
        var idReq= RequestBody.create("text/plain".toMediaTypeOrNull(),"dsa")

        var tagliststring="none"
        if(tagList.count()>0){
            tagliststring=""
            for(tag in tagList){
                tagliststring=tagliststring+tag+"|"
        }}
        var tagReq=RequestBody.create("text/plain".toMediaTypeOrNull(),tagliststring)

        val imagesParts = arrayOfNulls<MultipartBody.Part>(
            1
        )

            var imageBody= RequestBody.create("image/*".toMediaTypeOrNull(),f!!)
            imagesParts[0]= MultipartBody.Part.createFormData("images",f!!.name,imageBody)

        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var data=api.addPost("Bearer "+jwtString,imagesParts,idReq,descReq,locReq,tagReq)


        data.enqueue(object : retrofit2.Callback<PostPreview?> {
            override fun onResponse(call: Call<PostPreview?>, response: Response<PostPreview?>) {
                if(response.isSuccessful()){
                    progressDialog!!.dismiss()
                    val intent:Intent = Intent(this@ActivityCapturePost,ActivitySinglePost::class.java)
                    var b=Bundle()
                    b.putParcelable("selectedPost",response.body())
                    intent.putExtras(b)
                    startActivity(intent)
                    finish()
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







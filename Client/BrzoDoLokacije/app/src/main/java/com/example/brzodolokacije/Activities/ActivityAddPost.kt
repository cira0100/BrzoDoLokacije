package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
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
import java.io.File


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
    private lateinit var addLocation:Button
    private lateinit var tagLayout:LinearLayout
    private lateinit var tagButtons:List<Button>
    private lateinit var tagAutoText: AutoCompleteTextView
    val incorectCoord:Double=1000.0
    val LOCATIONREQCODE=123
    var longitude:Double=incorectCoord
    var latitude:Double=incorectCoord
    var progressDialog:ProgressDialog?=null
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

        uploadFromGallery=findViewById<View>(R.id.btnActivityAddPostUploadFromGalleryVisible) as Button
        showNextImage=findViewById<View>(R.id.nextImage) as Button
        showPreviousImage=findViewById<View>(R.id.previousImage) as Button
        switcher=findViewById<View>(R.id.isActivityAddPostSwitcher) as ImageSwitcher
        location=findViewById<View>(R.id.etActivityAddPostLocation) as EditText
        description=findViewById<View>(R.id.etActivityAddPostDescription) as EditText
        post=findViewById<View>(R.id.btnActivityAddPostPost) as Button
        addLocation=findViewById<View>(R.id.btnActivityAddPostAddLocation) as Button

        val tags=resources.getStringArray(R.array.Tags)
        //Log.d("Main",tags[0].toString())
        val tagadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,tags)
        tagAutoText= findViewById(R.id.acTags) as AutoCompleteTextView
        tagAutoText.setAdapter(tagadapter)

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Molimo sacekajte!!!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)


        switcher?.setFactory{
            val imgView = ImageView(applicationContext)
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            imgView.setPadding(8, 8, 8, 8)
            imgView}
        addLocation.setOnClickListener {
            val myIntent = Intent(this, MapsActivity::class.java)
            startActivityForResult(myIntent,LOCATIONREQCODE)
        }

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
                location.hint="Unesite naziv lokaciju"
                location.setHintTextColor(Color.RED)
            }
            if(descriptionString.isEmpty()) {
                description.hint="Unesite lokaciju"
                description.setHintTextColor(Color.RED)
            }
            if(longitude!=incorectCoord && latitude!=incorectCoord){
                Toast.makeText(this,"Unesite lokaciju klikom na dugme",Toast.LENGTH_LONG)
            }

            if(!locationString.isEmpty() && !descriptionString.isEmpty() && longitude!=incorectCoord && latitude!=incorectCoord && uploadedImages!!.size>0){
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
                uploadFromGallery.isVisible=false
                place=0
                //jedna slika
            } else if (data?.getData() != null) {
                uploadedImages!!.add(data.data!!)

                //prikaz jedne ucitane
                switcher.setImageURI(data.data!!)
                uploadFromGallery.isVisible=false
            }
        }
        if(requestCode==LOCATIONREQCODE && resultCode== RESULT_OK){
            var bundle=data!!.extras
            longitude=bundle!!.getDouble("longitude",incorectCoord)
            latitude=bundle!!.getDouble("latitude",incorectCoord)
        }
    }
    private fun sendPost(){
        uploadLocation()

    }
    fun uploadLocation() {
        //TO DO SEARCH EXISTING LOCATION FROM DB
        //IF NOT EXISTS ADD NEW LOCATION
        progressDialog!!.show()
        val api =RetrofitHelper.getInstance()
        var geocoder=GeocoderHelper.getInstance()
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
        val api =RetrofitHelper.getInstance()
        var desc=descriptionString
        description.text.clear()
        //loc
        //desc
        var locReq=RequestBody.create("text/plain".toMediaTypeOrNull(),loc)
        var descReq=RequestBody.create("text/plain".toMediaTypeOrNull(),desc)
        var idReq=RequestBody.create("text/plain".toMediaTypeOrNull(),"dsa")
        val imagesParts = arrayOfNulls<MultipartBody.Part>(
            uploadedImages!!.size
        )

        //dodavanje u bazu
        for (i in 0..uploadedImages!!.size - 1){
            //var file=File(uploadedImages!![i]!!.path)
            Log.d("Main", uploadedImages!![i]!!.path!!)

            var inputStream=getContentResolver().openInputStream(uploadedImages!![i]!!)
            val file: File = File.createTempFile("temp",i.toString())
            file!!.writeBytes(inputStream!!.readBytes())


            var imageBody=RequestBody.create("image/*".toMediaTypeOrNull(),file)
            imagesParts[i]=MultipartBody.Part.createFormData("images",file.name,imageBody)
        }
        var jwtString= SharedPreferencesHelper.getValue("jwt",this)
        var data=api.addPost("Bearer "+jwtString,imagesParts,idReq,descReq,locReq)


        data.enqueue(object : retrofit2.Callback<PostPreview?> {
            override fun onResponse(call: Call<PostPreview?>, response: Response<PostPreview?>) {
                progressDialog!!.dismiss()
                if(response.isSuccessful()){
                    Toast.makeText(
                        applicationContext, "USPEH", Toast.LENGTH_LONG
                    ).show();
                }else {

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
                Toast.makeText(
                    applicationContext, t.toString(), Toast.LENGTH_LONG
                ).show()
                progressDialog!!.dismiss()
                Log.d("Main",t.toString())
            }
        })
    }

}
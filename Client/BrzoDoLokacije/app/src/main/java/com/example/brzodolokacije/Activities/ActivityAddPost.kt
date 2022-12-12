package com.example.brzodolokacije.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
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
    private lateinit var tagButtons:MutableList<Button>
    private lateinit var tagText: EditText
    private lateinit var tagButtonAdd:Button
    private lateinit var tagList: MutableList<String>
    private lateinit var locText: EditText
    private var tagidcounter:Int = 0
    val LOCATIONREQCODE=123
    var locationId:String?=null
    var progressDialog:ProgressDialog?=null
    private lateinit var addDescription:Button



    private var place=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        uploadedImages= ArrayList()
        tagList = mutableListOf()
        tagButtons= mutableListOf()
        tagidcounter = 0

        uploadFromGallery=findViewById<View>(R.id.btnActivityAddPostUploadFromGalleryVisible) as Button
        showNextImage=findViewById<View>(R.id.nextImage) as Button
        showPreviousImage=findViewById<View>(R.id.previousImage) as Button
        switcher=findViewById<View>(R.id.isActivityAddPostSwitcher) as ImageSwitcher
        description=findViewById<View>(R.id.etActivityAddPostDescription) as EditText
        post=findViewById<View>(R.id.btnActivityAddPostPost) as Button
        addLocation=findViewById<View>(R.id.btnActivityAddPostAddLocation) as Button
        tagText =findViewById<View>(R.id.acTags) as EditText
        tagButtonAdd = findViewById<View>(R.id.btnActivityAddPostAddTag) as Button
        tagLayout =  findViewById<View>(R.id.llTags) as LinearLayout
        locText=findViewById<View>(R.id.etActivityAddPostLocationText) as EditText

        addDescription=findViewById<View>(R.id.tvActivityAddPostDescriptiontext)as Button

        tagText.isGone=true
        tagText.isVisible=false
        description.isGone=true
        description.isVisible=false

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
        addDescription.setOnClickListener {
            description.isGone=false
            description.isVisible=true
            description.requestFocus()
            showKeyboard(description)
        }
        //dodavanje i brisanje tagova
        tagButtonAdd.setOnClickListener {
           addTag()
            tagText.requestFocus()
            showKeyboard(tagText)
        }
        tagText.setOnKeyListener(View.OnKeyListener { v1, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_UP &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                addTag()
                tagText.requestFocus()
                showKeyboard(tagText)
                return@OnKeyListener true
            }
            false
        })

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
            //locationString=location.text.toString().trim()
            descriptionString=description.text.toString().trim()
            //prazan unos?
           /* if(locationString.isEmpty()) {
                location.hint="Unesite naziv lokaciju"
                location.setHintTextColor(Color.RED)
            }*/
            if(descriptionString.isEmpty()) {
                description.hint="Unesite opis"
                description.setHintTextColor(Color.RED)
            }
            if(locationId==null || locationId!!.trim()==""){
                Toast.makeText(this@ActivityAddPost,"Unesite lokaciju klikom na dugme",Toast.LENGTH_LONG).show()
            }
            if(uploadedImages==null ||uploadedImages!!.size<=0)
            {
                Toast.makeText(this@ActivityAddPost,"Unesite fotografije",Toast.LENGTH_LONG).show()
            }

            if(!descriptionString.isEmpty()  && uploadedImages!!.size>0){
                sendPost()
            }
        }
    }

    fun showKeyboard(item:EditText){
        var imm: InputMethodManager =this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(item, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(item: EditText){
        var imm: InputMethodManager =this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(item.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
            locationId=bundle!!.getString("locationId")
            var name=bundle!!.getString("name")
            locText.isGone=false
            locText.isVisible=true
            locText.setText(name,TextView.BufferType.EDITABLE)
        }
    }
    private fun sendPost(){
        uploadLocation()

    }
    fun uploadLocation() {
        if(locationId!=null && locationId!!.trim()!="")
            uploadPost(locationId!!)
    }
    fun uploadPost(loc:String){
        progressDialog!!.show()
        val api =RetrofitHelper.getInstance()
        var desc=descriptionString
        description.text.clear()
        //loc
        //desc
        var locReq=RequestBody.create("text/plain".toMediaTypeOrNull(),loc)
        var descReq=RequestBody.create("text/plain".toMediaTypeOrNull(),desc)
        var idReq=RequestBody.create("text/plain".toMediaTypeOrNull(),"dsa")

        var tagliststring="none"
        if(tagList.count()>0){
            tagliststring=""
            for(tag in tagList){
                tagliststring=tagliststring+tag+"|"
            }}
        var tagReq=RequestBody.create("text/plain".toMediaTypeOrNull(),tagliststring)

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
        var data=api.addPost("Bearer "+jwtString,imagesParts,idReq,descReq,locReq,tagReq)
        //multipart formdata : images , _id , description , locationId , tags

        data.enqueue(object : retrofit2.Callback<PostPreview?> {
            override fun onResponse(call: Call<PostPreview?>, response: Response<PostPreview?>) {
                progressDialog!!.dismiss()
                if(response.isSuccessful()){
                    val intent:Intent = Intent(this@ActivityAddPost,ActivitySinglePost::class.java)
                    var b=Bundle()
                    b.putParcelable("selectedPost",response.body())
                    intent.putExtras(b)
                    startActivity(intent)
                    finish()
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
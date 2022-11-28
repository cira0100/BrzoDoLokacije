package com.example.brzodolokacije.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProfile : Fragment(com.example.brzodolokacije.R.layout.fragment_profile) {
    // TODO: Rename and change types of parameters
    private lateinit var username: TextView
    private lateinit var name: TextView
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount:TextView
    private lateinit var usernameString: String
    private lateinit var nameString: String
    private lateinit var postsCountString: String
    private lateinit var followersCountString: String
    private lateinit var followingCountString:String
    private lateinit var showMyPosts: Button
    private lateinit var showMyData: Button
    private lateinit var showMyRecensions: Button
    private lateinit var profilePicture: ImageView
    private lateinit var profilePicturePlus: MaterialButton
    private lateinit var showFollowers: TextView
    private lateinit var showFollowing: TextView

    var userId:String = "1"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Toast.makeText(
//            activity, "MOJ PROFILLLLLLLLLLLLLLL", Toast.LENGTH_LONG
//        ).show();
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(com.example.brzodolokacije.R.layout.fragment_profile, container, false)
        name = view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfileName) as TextView
        username = view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfileUserName) as TextView
        postsCount = view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfilePostsNo) as TextView
        followersCount = view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfileFollowersNo) as TextView
        followingCount = view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfileFollowNo) as TextView
        showMyPosts=view.findViewById<View>(com.example.brzodolokacije.R.id.btnFragmentProfileShowMyPosts) as Button
        showMyData=view.findViewById<View>(com.example.brzodolokacije.R.id.btnFragmentProfileShowMyData) as Button
        showMyRecensions=view.findViewById<View>(com.example.brzodolokacije.R.id.btnFragmentProfileShowMyRecensions) as Button
        profilePicture=view.findViewById<View>(com.example.brzodolokacije.R.id.tvFragmentProfileProfilePicture) as ImageView
        profilePicturePlus=view.findViewById<View>(com.example.brzodolokacije.R.id.btnFragmentProfileProfilePicturePlus) as MaterialButton
        showFollowers=view.findViewById(com.example.brzodolokacije.R.id.tvFragmentProfileFollowers)
        showFollowing=view.findViewById(com.example.brzodolokacije.R.id.tvFragmentProfileFollow)
        //podaci iz baze



        showMyPosts.setOnClickListener{

            openMyPosts()
        }


        showMyData.setOnClickListener{

            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(com.example.brzodolokacije.R.id.flFragmentProfileFragmentContainer, FragmentMyProfileInfo())
            fm.commit()
        }

        showMyRecensions.setOnClickListener{
            getProfileInfo()
            var fm: FragmentTransaction =childFragmentManager.beginTransaction()

            fm.replace(com.example.brzodolokacije.R.id.flFragmentProfileFragmentContainer, FragmentMyRecensions())
            fm.commit()
        }
        profilePicturePlus.setOnClickListener{
            addProfilePicture()
        }

        showFollowers.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId",userId )
            val fragmentFollowers = FragmentFollowers()
            fragmentFollowers.setArguments(bundle)

            fragmentManager
                ?.beginTransaction()
                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentFollowers)
                ?.commit()
        }

        showFollowing.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId",userId)
            val fragmentFollowing = FragmentFollowing()
            fragmentFollowing.setArguments(bundle)

            fragmentManager
                ?.beginTransaction()
                ?.replace(com.example.brzodolokacije.R.id.flNavigationFragment,fragmentFollowing)
                ?.commit()
        }
        getProfileInfo()
        openMyPosts()
        return view
    }
    fun openMyPosts(){
        var fm: FragmentTransaction =childFragmentManager.beginTransaction()

        fm.replace(com.example.brzodolokacije.R.id.flFragmentProfileFragmentContainer, FragmentUserPosts())
        fm.commit()
    }

    private fun addProfilePicture(){
        val intent= Intent(Intent.ACTION_PICK)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type="image/*"
        startActivityForResult(Intent.createChooser(intent,"Izaberi profilnu sliku"),201)
    }
    private fun uploadProfilePicture(imageUri:Uri){
        val api =RetrofitHelper.getInstance()
        var inputStream=requireActivity().getContentResolver().openInputStream(imageUri)
        val file: File = File.createTempFile("temp","pfp")
        file!!.writeBytes(inputStream!!.readBytes())
        var imageReq=RequestBody.create("image/*".toMediaTypeOrNull(),file)
        val imageBody: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, imageReq)
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val req=api.setPfp("Bearer "+token,imageBody)

        req.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(response.isSuccessful()){
                   getProfileInfo()
                }else{
                    if(response.errorBody()!=null)
                        Toast.makeText(activity, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(
                    activity, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //nakon otvaranja
        if(requestCode==201 && resultCode== AppCompatActivity.RESULT_OK){
            var imageUri=data!!.data
            uploadProfilePicture(imageUri!!)

        }
    }

    private fun getProfileInfo(){
        val authApi=RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request=authApi.selfProfile("Bearer "+token)

        request.enqueue(object : retrofit2.Callback<UserReceive?> {
            override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                if(response.isSuccessful()){
                     setUserInfo(response.body()!!)
                }else{
                    if(response.errorBody()!=null)
                        Toast.makeText(activity, response.errorBody()!!.string(), Toast.LENGTH_LONG).show();
                }
            }
            override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                Toast.makeText(
                    activity, t.toString(), Toast.LENGTH_LONG
                ).show();
            }
        })
    }
    private fun setUserInfo(user:UserReceive){
        name.setText(user.name)
        username.setText("@"+user.username)
        postsCount.setText(user.postcount.toString())

        followersCount.setText(user.followersNumber.toString())
        followingCount.setText(user.followingNumber.toString())

        userId=user._id

        //Add Profile image
        if(user.pfp!=null) {
            Glide.with(requireActivity())
                .load(RetrofitHelper.baseUrl + "/api/post/image/" + user.pfp!!._id)
                .circleCrop()//Round image
                .into(profilePicture)
        }
    }


}
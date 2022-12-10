package com.example.brzodolokacije.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Models.CommentReceive
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class FragmentSinglePostComments : Fragment() {

    private lateinit var commentsContainer:RecyclerView
    private lateinit var commentsCount: TextView
    private lateinit var newComment: EditText
    private lateinit var postComment: ImageView
    private lateinit var post:PostPreview

    private var layoutManagerComments: RecyclerView.LayoutManager? = null
    private var adapterComments: RecyclerView.Adapter<CommentsAdapter.ViewHolder>? = null
    private var recyclerViewComments: RecyclerView?=null
    private var comments:MutableList<CommentSend>?=mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_single_post_comments, container, false)

        var args = arguments
        var jsonPostObject = args!!.getString("post")
        post= Gson().fromJson(jsonPostObject, PostPreview::class.java)

        commentsContainer=view.findViewById(R.id.rvComments)
        commentsCount=view.findViewById(R.id.tvCommentCount)
        newComment=view.findViewById(R.id.NewComment)
        postComment=view.findViewById(R.id.btnPostComment)

        buildRecyclerViewComments()
        requestGetComments()

        postComment.setOnClickListener {
            if(newComment.text.isNotEmpty()){
                val comment=CommentReceive(newComment.text.toString(),"")
                requestAddComment(comment)


            }
            else{
                Toast.makeText(requireActivity(),"Unesite tekst komentara.",Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    fun requestAddComment(comment: CommentReceive){
        val postApi= RetrofitHelper.getInstance()
        val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
        val request=postApi.addComment("Bearer "+token,post._id,comment)
        request.enqueue(object : retrofit2.Callback<CommentSend?> {
            override fun onResponse(call: Call<CommentSend?>, response: Response<CommentSend?>) {
                if(response.isSuccessful){

                    var newComment1=response.body()!!
                    requestGetComments(newComment1)
                    newComment.text.clear()
                    hideKeyboard(newComment)

                }else{
                    if(response.errorBody()!=null)
                        Log.d("main1",response.message().toString())
                }


            }

            override fun onFailure(call: Call<CommentSend?>, t: Throwable) {
                Log.d("main2",t.message.toString())
            }
        })
    }
    fun requestGetComments(newComment:CommentSend?=null){
        if(newComment==null){
            val postApi= RetrofitHelper.getInstance()
            val token= SharedPreferencesHelper.getValue("jwt", requireActivity())
            val request=postApi.getComments("Bearer "+token,post._id)
            request.enqueue(object : retrofit2.Callback<MutableList<CommentSend>?> {
                override fun onResponse(call: Call<MutableList<CommentSend>?>, response: Response<MutableList<CommentSend>?>) {
                    if(response.isSuccessful){
                        comments= response.body()!!
                        if(comments!=null && comments!!.isNotEmpty()){
                            buildRecyclerViewComments()
                            if(comments!=null)
                                commentsCount.text=countComments(comments!!).toString()
                            else
                                commentsCount.text="12"
                        }
                    }else{
                        if(response.errorBody()!=null)
                            Log.d("main1",response.message().toString())
                    }


                }

                override fun onFailure(call: Call<MutableList<CommentSend>?>, t: Throwable) {
                    Log.d("main2",t.message.toString())
                }
            })
        }
        else{
            (adapterComments as CommentsAdapter).items.add(0,newComment)
            recyclerViewComments?.adapter=adapterComments
            addedComment()
        }
    }

    fun buildRecyclerViewComments(){
        recyclerViewComments=commentsContainer
        adapterComments=CommentsAdapter(comments as MutableList<CommentSend>,requireActivity(),this@FragmentSinglePostComments)
        layoutManagerComments= LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        recyclerViewComments!!.setHasFixedSize(false)
        recyclerViewComments!!.layoutManager=layoutManagerComments
        recyclerViewComments!!.adapter= adapterComments
    }
    fun hideKeyboard(item: EditText){
        var imm: InputMethodManager =activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(item.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun countComments(comments:List<CommentSend>):Int{
        var count:Int=0
        for(c in comments){
            if(c.replies!=null)
                count=count+countComments(c.replies!!)
            count=count+1
        }
        return count
    }

   fun addedComment(){
        commentsCount.text=(Integer.parseInt(commentsCount.text.toString())+1).toString()
        commentsCount.invalidate()
    }

}

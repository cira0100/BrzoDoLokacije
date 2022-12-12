package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Activities.ActivityUserProfile
import com.example.brzodolokacije.Fragments.FragmentSinglePostComments
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.CommentReceive
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.SingleCommentBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.single_comment.view.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class CommentsAdapter (val items : MutableList<CommentSend>,val activity: Activity, val fragment:FragmentSinglePostComments)
    : RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private var api: IBackendApi?=null
    private var token:String?=null
    private lateinit var binding: SingleCommentBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=SingleCommentBinding.inflate(inflater,parent,false)
        api=RetrofitHelper.getInstance()
        token=SharedPreferencesHelper.getValue("jwt",activity)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        try{
            return items.size
        }catch (e:Exception){
            return 0
        }
    }
    inner class ViewHolder(itemView : SingleCommentBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : CommentSend){
            binding.apply {
                var color = ContextCompat.getColor(activity, R.color.purple_500)
                etReplyCount.setTextColor(color)
                tvCommentAuthor.text=item.username
                tvCommentText.text=item.comment
                Log.d("info",tvCommentText.text.toString()+binding.toString())
                requestProfilePic(item)
                llReply.visibility=View.GONE
                llReply.forceLayout()
                if(item.parentId!=""){
                    btnReply.visibility=View.GONE
                    btnReply.forceLayout()
                }
                else{
                    btnReply.setOnClickListener {
                        llReply.visibility=View.VISIBLE
                        llReply.forceLayout()
                        etReply.requestFocus()
                    }
                }
                etReply.showSoftInputOnFocus=true
                etReply.setOnFocusChangeListener { _, focused ->
                    if(!focused){
                        llReply.visibility= View.GONE
                        llReply.forceLayout()
                        //btnReply.setImageResource(R.drawable.)
                        hideKeyboard(etReply)
                    }
                    else{
                        showKeyboard(etReply)
                        btnPostReply.setOnClickListener{
                            if(etReply.text!!.isNotEmpty()){
                                val postId=(activity as ActivitySinglePost).post._id
                                Log.d("main",binding.toString())
                                val comment= CommentReceive(etReply.text.toString(),item._id)
                                requestAddComment(comment,postId)
                            }
                            else{
                              Log.d("komentari","greska")
                            }
                        }
                    }
                }
                ivPfp.setOnClickListener {
                    val intent: Intent = Intent(activity, ActivityUserProfile::class.java)
                    var b= Bundle()
                    intent.putExtra("user", Gson().toJson(
                        UserReceive(item.userId,"",item.username,"",
                            Date(),null,0, listOf(),0,listOf(),0,listOf(),0,null)
                    ))
                    activity.startActivity(intent)
                }


                var rv: RecyclerView = rvReplies
                rv.setHasFixedSize(true)
                rv.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
                if(item.replies!=null){
                    setReplyCount(layoutPosition)
                    etReplyCount.setOnClickListener {
                        if(llReplies.visibility==View.VISIBLE)
                            llReplies.visibility=View.GONE
                        else
                            llReplies.visibility=View.VISIBLE
                        llReplies.forceLayout()
                    }
                    rv.adapter=CommentsAdapter(item.replies as MutableList<CommentSend>,activity,fragment)
                }
                else
                    rv.adapter=CommentsAdapter(mutableListOf(),activity,fragment)
            }
        }
        fun setReplyCount(position: Int){

            if(items[position].replies!!.count()==1)
                itemView.etReplyCount.text=items[position].replies!!.count().toString() + " odgovor"
            else
                itemView.etReplyCount.text=items[position].replies!!.count().toString() + " odgovora"
            itemView.clReplyCount.visibility=View.VISIBLE
            itemView.clReplyCount.invalidate()
        }
        fun showKeyboard(item:EditText){
            var imm:InputMethodManager=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(item,InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideKeyboard(item: EditText){
            var imm:InputMethodManager=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(item.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
        }
        fun requestAddComment(comment:CommentReceive,postId:String){
            val postApi= RetrofitHelper.getInstance()
            val token= SharedPreferencesHelper.getValue("jwt", activity)
            val request=postApi.addComment("Bearer "+token,postId,comment)
            request.enqueue(object : retrofit2.Callback<CommentSend?> {
                override fun onResponse(call: Call<CommentSend?>, response: Response<CommentSend?>) {
                    if(response.isSuccessful){
                        var newComment=response.body()!!
                        requestGetComments(newComment)
                        itemView.etReply.text!!.clear()
                        hideKeyboard(itemView.etReply)
                        itemView.etReply.clearFocus()
                        setReplyCount(bindingAdapterPosition)
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

        private fun requestGetComments(newComment: CommentSend) {
            var rv: RecyclerView = itemView.rvReplies
            var adapter:CommentsAdapter=rv.adapter as CommentsAdapter
            adapter.items.add(0,newComment)
            rv.adapter=adapter
            fragment.addedComment()
        }

        private fun requestProfilePic(item:CommentSend){
            val request2=api?.getProfileFromId("Bearer "+token,
                item.userId
            )
            request2?.enqueue(object : retrofit2.Callback<UserReceive?> {
                override fun onResponse(
                    call: Call<UserReceive?>,
                    response: Response<UserReceive?>
                ) {
                    if (response.isSuccessful) {
                        var user = response.body()!!
                        if (user.pfp != null) {
                            Glide.with(activity)
                                .load(RetrofitHelper.baseUrl + "/api/post/image/compress/" + user.pfp!!._id)
                                .circleCrop()
                                .into(itemView.ivPfp)
                        }
                    } else {
                        Toast.makeText(
                            activity, "los id",
                            Toast.LENGTH_LONG
                        ).show()
                        itemView.tvCommentAuthor.text = "nije nadjen korisnik"
                    }
                }

                override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                    Toast.makeText(
                        activity, "neuspesan zahtev",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}
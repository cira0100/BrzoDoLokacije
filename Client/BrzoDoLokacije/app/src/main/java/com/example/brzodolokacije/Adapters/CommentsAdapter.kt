package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Models.CommentReceive
import com.example.brzodolokacije.Models.CommentSend
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.SingleCommentBinding
import retrofit2.Call
import retrofit2.Response

class CommentsAdapter (val items : MutableList<CommentSend>,val activity: Activity)
    : RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: SingleCommentBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=SingleCommentBinding.inflate(inflater,parent,false)
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
                tvCommentAuthor.text=item.username
                tvCommentText.text=item.comment
                etReply.visibility= View.GONE
                etReply.forceLayout()
                etReply.showSoftInputOnFocus=true
                etReply.setOnFocusChangeListener { _, focused ->
                    if(!focused){
                        etReply.visibility= View.GONE
                        etReply.forceLayout()
                        btnReply.setImageResource(R.drawable.reply)
                        hideKeyboard(etReply)
                    }
                    else{
                        showKeyboard(etReply)
                        btnReply.setImageResource(R.drawable.post_comment)
                        btnReply.setOnClickListener{
                            if(etReply.text.isNotEmpty()){
                                val postId=(activity as ActivitySinglePost).post._id
                                val comment= CommentReceive(etReply.text.toString(),item._id)
                                requestAddComment(binding,comment,postId)
                            }
                            else{
                              Log.d("komentari","greska")
                            }
                        }
                    }
                }
                btnReply.setOnClickListener {
                    etReply.visibility=View.VISIBLE
                    etReply.forceLayout()
                    etReply.requestFocus()
                }

                var rv: RecyclerView = rvReplies
                rv.setHasFixedSize(false)
                rv.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
                if(item.replies!=null)
                    rv.adapter=CommentsAdapter(item.replies as MutableList<CommentSend>,activity)
                else
                    rv.adapter=CommentsAdapter(mutableListOf(),activity)
            }
        }
        fun showKeyboard(item:EditText){
            var imm:InputMethodManager=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(item,InputMethodManager.SHOW_FORCED)
        }

        fun hideKeyboard(item: EditText){
            var imm:InputMethodManager=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(item.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        fun requestAddComment(binding:SingleCommentBinding,comment:CommentReceive,postId:String){
            val postApi= RetrofitHelper.getInstance()
            val token= SharedPreferencesHelper.getValue("jwt", activity)
            val request=postApi.addComment("Bearer "+token,postId,comment)
            request.enqueue(object : retrofit2.Callback<CommentSend?> {
                override fun onResponse(call: Call<CommentSend?>, response: Response<CommentSend?>) {
                    if(response.isSuccessful){
                        var newComment=response.body()!!
                        requestGetComments(binding,newComment)
                        binding.etReply.text.clear()
                        hideKeyboard(binding.etReply)
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

        private fun requestGetComments(binding:SingleCommentBinding,newComment: CommentSend) {
            var rv: RecyclerView = binding.rvReplies
            var adapter:CommentsAdapter=rv.adapter as CommentsAdapter
            adapter.items.add(0,newComment)
            rv.adapter=adapter
            //(activity as ActivitySinglePost).addedComment()
        }
    }
}
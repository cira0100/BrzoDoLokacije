package com.example.brzodolokacije.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brzodolokacije.Activities.ChatActivity
import com.example.brzodolokacije.Activities.ChatActivityConversation
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.Models.UserReceive
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.ChatPreviewBinding
import kotlinx.android.synthetic.main.chat_preview.view.*
import retrofit2.Call
import retrofit2.Response

class ChatPreviewsAdapter (val items : MutableList<ChatPreview>,val activity:ChatActivity)
    : RecyclerView.Adapter<ChatPreviewsAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private var api: IBackendApi?=null
    private var token:String?=null
    private lateinit var binding: ChatPreviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=ChatPreviewBinding.inflate(inflater,parent,false)
        api=RetrofitHelper.getInstance()
        token=SharedPreferencesHelper.getValue("jwt",activity)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(activity, ChatActivityConversation::class.java)
            intent.putExtra("userId",items[position].userId)
            intent.putExtra("username",holder.itemView.tvUsername.text)
            activity.startActivity(intent)
        }
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : ChatPreviewBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : ChatPreview){
            binding.apply {
                val request2=api?.getProfileFromId("Bearer "+token,
                    item.userId
                )
                request2?.enqueue(object : retrofit2.Callback<UserReceive?> {
                    override fun onResponse(call: Call<UserReceive?>, response: Response<UserReceive?>) {
                        if(response.isSuccessful()){
                            //zahtev da se posalje poruka
                            var user=response.body()!!
                            tvUsername.text=user.username
                            if(user.pfp!=null) {
                                Glide.with(activity)
                                    .load(RetrofitHelper.baseUrl + "/api/post/image/" + user.pfp!!._id)
                                    .into(ivUserImage)
                            }
                        }
                        else{
                            Toast.makeText(activity,"los id",
                                Toast.LENGTH_LONG).show()
                            tvUsername.text="nije nadjen korisnik"
                        }
                    }

                    override fun onFailure(call: Call<UserReceive?>, t: Throwable) {
                        Toast.makeText(activity,"neuspesan zahtev",
                            Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}
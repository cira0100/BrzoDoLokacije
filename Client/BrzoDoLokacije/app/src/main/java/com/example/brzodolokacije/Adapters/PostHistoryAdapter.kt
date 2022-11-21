package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ActivitySinglePost
import com.example.brzodolokacije.Interfaces.IBackendApi
import com.example.brzodolokacije.Models.LocationType
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import com.example.brzodolokacije.databinding.SinglePostHistoryBinding


class PostHistoryAdapter (val activity:Activity,val items : MutableList<PostPreview>)
    : RecyclerView.Adapter<PostHistoryAdapter.ViewHolder>() {
    private lateinit var token: String
    private lateinit var imageApi: IBackendApi

    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: SinglePostHistoryBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        imageApi= RetrofitHelper.getInstance()
        token= SharedPreferencesHelper.getValue("jwt", activity).toString()
        binding = SinglePostHistoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }




    override fun getItemCount() = items.size
    inner class ViewHolder(itemView: SinglePostHistoryBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: PostPreview) {
            binding.apply {
                tvTitleSinglePostHistory.text = item.location.name
                tvLocationParentSinglePostHistory.text = "22.11.2022"
                tvLocationTypeSinglePostHistory.text = item.location.country

            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            //Toast.makeText(activity,item._id,Toast.LENGTH_LONG).show()
            val intent:Intent = Intent(activity,ActivitySinglePost::class.java)
            var b=Bundle()
            items[position].location.type=LocationType.ADA
            b.putParcelable("selectedPost", items[position])
            intent.putExtras(b)
            activity.startActivity(intent)
        }
    }
}

package com.example.brzodolokacije.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Models.ListItemModel
import com.example.brzodolokacije.databinding.ListItemBinding

class SampleAdapter (val items : MutableList<ListItemModel>)
    : RecyclerView.Adapter<SampleAdapter.ViewHolder>(){
    //constructer has one argument - list of objects that need to be displayed
    //it is bound to xml of single item
    private lateinit var binding: ListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding=ListItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        //sets components of particular item
        holder.bind(items[position])
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : ListItemBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : ListItemModel){
            binding.apply {
                tvId.text=item.id.toString()
                tvName.text=item.name
            }
        }
    }
}
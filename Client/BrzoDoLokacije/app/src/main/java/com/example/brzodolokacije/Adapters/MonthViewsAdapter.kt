package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Models.MonthlyViews
import com.example.brzodolokacije.databinding.SingleDateViewBinding

class MonthViewsAdapter (val activity: Activity, val items : MutableList<MonthlyViews>)
    : RecyclerView.Adapter<MonthViewsAdapter.ViewHolder>() {


    private lateinit var binding: SingleDateViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = SingleDateViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }




    override fun getItemCount() = items.size
    inner class ViewHolder(itemView: SingleDateViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: MonthlyViews) {
            binding.apply {
                tvMonth.text=numberToMonthName(item.month)
                tvMonthViews.text=item.views.toString()

            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(items[position])

    }
    fun numberToMonthName(number:Int):String{
        var text=""
        when (number) {
            1 -> text="Januar"
            2 -> text="Februar"
            3 -> text="Mart"
            4 -> text="April"
            5 -> text="Maj"
            6 -> text="Jun"
            7 -> text="Jul"
            8 -> text="Avgust"
            9 -> text="Septembar"
            10 -> text="Oktobar"
            11 -> text="Novembar"
            12 -> text="Decembar"
            else -> {
                text = "nedefinisan"
            }
        }

        return text

    }

}

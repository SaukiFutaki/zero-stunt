package com.saukikikiki.zerostunt.ui.nutrition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saukikikiki.zerostunt.databinding.ItemSummaryBinding
import com.saukikikiki.zerostunt.ui.nutrition.NutritionResultFragment.Nutrition

class SummaryAdapter(private val nutritions: List<Nutrition>) :
    RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nutrition = nutritions[position]
        holder.binding.tvNameAndUnit.text = nutrition.nameAndUnit
        holder.binding.tvValue.text = nutrition.value
    }

    override fun getItemCount(): Int = nutritions.size
}
package com.saukikikiki.zerostunt.ui.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saukikikiki.zerostunt.databinding.ItemNutritionDetailBinding
import com.saukikikiki.zerostunt.ui.scan.ScanResultFragment

class NutritionAdapter(private val nutritions: List<ScanResultFragment.Nutrition>) :
    RecyclerView.Adapter<NutritionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNutritionDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNutritionDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nutrition = nutritions[position]
        holder.binding.tvNutritionName.text = nutrition.name
        holder.binding.tvNutritionValue.text = nutrition.value.toString()
    }

    override fun getItemCount(): Int = nutritions.size
}
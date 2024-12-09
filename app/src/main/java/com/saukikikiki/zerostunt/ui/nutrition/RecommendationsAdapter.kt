package com.saukikikiki.zerostunt.ui.nutrition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saukikikiki.zerostunt.databinding.ItemRecommendationBinding
import com.saukikikiki.zerostunt.ui.nutrition.NutritionResultFragment.Recommendation

class RecommendationsAdapter(private val recommendations: List<Recommendation>) :
    RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.binding.tvCombination.text = recommendation.combination

        if (recommendation.foods.size > 0) {
            holder.binding.tvFood1.text = "Food 1: ${recommendation.foods[0]}"
        }
        if (recommendation.foods.size > 1) {
            holder.binding.tvFood2.text = "Food 2: ${recommendation.foods[1]}"
        }
        if (recommendation.foods.size > 2) {
            holder.binding.tvFood3.text = "Food 3: ${recommendation.foods[2]}"
        }

    }

    override fun getItemCount(): Int = recommendations.size
}
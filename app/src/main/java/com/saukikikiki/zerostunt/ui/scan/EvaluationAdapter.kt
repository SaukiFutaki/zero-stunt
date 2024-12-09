package com.saukikikiki.zerostunt.ui.scan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saukikikiki.zerostunt.databinding.ItemEvaluationBinding

class EvaluationAdapter(private val evaluations: List<ScanResultFragment.Evaluation>) :
    RecyclerView.Adapter<EvaluationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEvaluationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEvaluationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evaluation = evaluations[position]
        Log.d("EvaluationAdapter", "Evaluation name: ${evaluation.name}")
        Log.d("EvaluationAdapter", "Evaluation value: ${evaluation.value}")
        holder.binding.tvEvaluationName.text = evaluation.name
        holder.binding.tvEvaluationValue.text = evaluation.value


    }

    override fun getItemCount(): Int = evaluations.size
}
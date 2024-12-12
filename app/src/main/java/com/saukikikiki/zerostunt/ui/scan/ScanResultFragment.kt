package com.saukikikiki.zerostunt.ui.scan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.saukikikiki.zerostunt.databinding.FragmentScanResultBinding
import org.json.JSONObject
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saukikikiki.zerostunt.ui.nutrition.NutritionResultFragment.Nutrition
import org.json.JSONException


class ScanResultFragment : Fragment() {

    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val analyzedResult = arguments?.getString("analyzedResult") ?: ""
        Log.d("ScanResultFragment", "analyzedResult: $analyzedResult")

        try {
            // Parsing data JSON
            val jsonObject = JSONObject(analyzedResult)
            val prediction = jsonObject.getString("prediction")
            val nutrition = jsonObject.getJSONObject("nutrition")
            val evaluation = jsonObject.getJSONObject("evaluation")

            // Membuat list data untuk RecyclerView
            val nutritionList = mutableListOf<Nutrition>()
            val evaluationList = mutableListOf<Evaluation>()


            // Mengisi list nutritionList
            for (key in nutrition.keys()) {
                val value = nutrition.getString(key)
                nutritionList.add(Nutrition(key, value))
            }

            // Mengisi list evaluationList
            for (key in evaluation.keys()) {
                val value = evaluation.getString(key)
                evaluationList.add(Evaluation(key, value))
            }

            // Set up RecyclerView untuk nutrition details
            val nutritionAdapter = NutritionAdapter(nutritionList)
            binding.rvNutritionDetails.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNutritionDetails.adapter = nutritionAdapter

            // Set up RecyclerView untuk evaluation (notes)
            val evaluationAdapter = EvaluationAdapter(evaluationList)
            binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNotes.adapter = evaluationAdapter


            // binding text
            binding.tvPrediction.text = prediction

           binding.btnOk.setOnClickListener {
               findNavController().navigate(
                   ScanResultFragmentDirections.actionNavigationScanResultToNavigationScan()
               )
            }
        } catch (e: JSONException) {
            Log.e("ScanResultFragment", "Error parsing JSON: ${e.message}")
            Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    data class Evaluation(val name: String, val value: String)

    data class Nutrition(val name: String, val value: String)
}


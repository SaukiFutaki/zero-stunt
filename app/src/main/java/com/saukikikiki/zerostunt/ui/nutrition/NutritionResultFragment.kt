package com.saukikikiki.zerostunt.ui.nutrition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saukikikiki.zerostunt.databinding.FragmentNutritionResultBinding

class NutritionResultFragment : Fragment() {

    private var _binding: FragmentNutritionResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recommendations = arguments?.getString("recommendations") ?: ""
        val summaryText = arguments?.getString("summary") ?: ""

        // Ubah format recommendations dari Markdown ke plain text
        val formattedRecommendations = formatMarkdownToPlainText(recommendations)

        // Parsing data summary
        val nutritionList = parseSummary(summaryText)

        // Set up RecyclerView untuk recommendations
        val recommendationsAdapter = RecommendationsAdapter(parseRecommendations(formattedRecommendations))
        binding.rvRecommendations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecommendations.adapter = recommendationsAdapter

        // Set up RecyclerView untuk summary
        val summaryAdapter = SummaryAdapter(nutritionList)
        binding.rvSummary.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSummary.adapter = summaryAdapter

        binding.btnOk.setOnClickListener {
            findNavController().navigate(
                NutritionResultFragmentDirections.actionNavigationNutritionResultToNavigationNutrition()
            )
        }
    }

    // Fungsi untuk mengubah format Markdown ke plain text
    private fun formatMarkdownToPlainText(markdownText: String): String {
        var plainText = markdownText

        // Hapus heading
        plainText = plainText.replace(Regex("##+ "), "")

        // Hapus bold
        plainText = plainText.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")

        // Hapus list item
        plainText = plainText.replace(Regex("- "), "")

        // Ganti newline dengan spasi
        plainText = plainText.replace("\n", " ")

        // Hapus spasi berlebih
        plainText = plainText.trim().replace(Regex("\\s+"), " ")

        return plainText
    }

    private fun parseSummary(summaryText: String): List<Nutrition> {
        val nutritionList = mutableListOf<Nutrition>()
        val lines = summaryText.lines()
        for (line in lines) {
            val parts = line.split(":")
            if (parts.size == 2) {
                val nameAndUnit = parts[0].trim()
                val value = parts[1].trim()
                val nutrition = Nutrition(nameAndUnit, value)
                nutritionList.add(nutrition)
            }
        }
        return nutritionList
    }

    private fun parseRecommendations(recommendationsText: String): List<Recommendation> {
        val recommendationsList = mutableListOf<Recommendation>()
        // Pisahkan kombinasi berdasarkan pola "Combination X:"
        val combinations = recommendationsText.split(Regex("Combination [0-9]+:"))
        // Mulai dari indeks 1 karena indeks 0 adalah string kosong sebelum kombinasi pertama
        for (i in 1 until combinations.size) {
            val combination = "Combination ${i}"
            // Pisahkan makanan berdasarkan pola "Food Y:"
            val foods = combinations[i].split(Regex("Food [0-9]+:"))
                .filter { it.isNotBlank() } // Filter string kosong
                .map { it.trim() } // Rapikan spasi di awal dan akhir
            recommendationsList.add(Recommendation(combination, foods))
        }
        return recommendationsList
    }

    data class Nutrition(
        val nameAndUnit: String,
        val value: String
    )

    data class Recommendation(
        val combination: String,
        val foods: List<String>
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
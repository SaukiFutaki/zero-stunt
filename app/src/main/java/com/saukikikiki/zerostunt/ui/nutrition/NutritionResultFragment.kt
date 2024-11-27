package com.saukikikiki.zerostunt.ui.nutrition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        val menu1Makanan = arguments?.getString("menu1Makanan") ?: ""
        val menu1Lauk = arguments?.getString("menu1Lauk") ?: ""
        val menu2Makanan = arguments?.getString("menu2Makanan") ?: ""
        val menu2Lauk = arguments?.getString("menu2Lauk") ?: ""
        val menu3Makanan = arguments?.getString("menu3Makanan") ?: ""
        val menu3Lauk = arguments?.getString("menu3Lauk") ?: ""

       binding.tvTitle.text = menu1Makanan + " " + menu1Lauk + " " + menu2Makanan + " " + menu2Lauk + " " + menu3Makanan + " " + menu3Lauk

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
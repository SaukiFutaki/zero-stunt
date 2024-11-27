package com.saukikikiki.zerostunt.ui.nutrition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentNutritionBinding

class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (isInputValid()) {
                val menu1Makanan = binding.tilMenu1Makanan.editText?.text.toString()
                val menu1Lauk = binding.tilMenu1Lauk.editText?.text.toString()
                val menu2Makanan = binding.tilMenu2Makanan.editText?.text.toString()
                val menu2Lauk = binding.tilMenu2Lauk.editText?.text.toString()
                val menu3Makanan = binding.tilMenu3Makanan.editText?.text.toString()
                val menu3Lauk = binding.tilMenu3Lauk.editText?.text.toString()

                try {
                    val action = NutritionFragmentDirections.actionNavigationNutritionToNavigationNutritionResult(
                        menu1Makanan,
                        menu1Lauk,
                        menu2Makanan,
                        menu2Lauk,
                        menu3Makanan,
                        menu3Lauk
                    )
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isInputValid(): Boolean {
        val inputLayouts = arrayOf(
            binding.tilMenu1Makanan,
            binding.tilMenu2Makanan,
            binding.tilMenu3Makanan
        )

        for (layout in inputLayouts) {
            if (layout.editText?.text.toString().isEmpty()) {
                layout.error = "Jenis makanan wajib diisi"
                return false
            } else {
                layout.error = null
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
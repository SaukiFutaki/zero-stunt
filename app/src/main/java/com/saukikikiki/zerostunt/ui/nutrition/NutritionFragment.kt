package com.saukikikiki.zerostunt.ui.nutrition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.data.api.ApiClient
import com.saukikikiki.zerostunt.data.api.DetectRequest
import com.saukikikiki.zerostunt.data.api.DetectResponse
import com.saukikikiki.zerostunt.databinding.FragmentNutritionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        binding.fabHelp.setOnClickListener(){
            showHelpDialog()
        }

        binding.btnSubmit.setOnClickListener {
            if (isInputValid()) {
                val menu1Makanan = binding.tilMenu1Makanan.editText?.text.toString()
                val menu1Lauk = binding.tilMenu1Lauk.editText?.text.toString()
                val menu2Makanan = binding.tilMenu2Makanan.editText?.text.toString()
                val menu2Lauk = binding.tilMenu2Lauk.editText?.text.toString()
                val menu3Makanan = binding.tilMenu3Makanan.editText?.text.toString()
                val menu3Lauk = binding.tilMenu3Lauk.editText?.text.toString()

                val foods = listOf(menu1Makanan, menu1Lauk, menu2Makanan, menu2Lauk, menu3Makanan, menu3Lauk)

                detectAndEvaluate(foods)
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

    private fun detectAndEvaluate(foods: List<String>) {
        val detectRequest = DetectRequest(foods)
        ApiClient.detectAndEvaluateService.detect(detectRequest).enqueue(object : Callback<DetectResponse> {
            override fun onResponse(call: Call<DetectResponse>, response: Response<DetectResponse>) {
                if (response.isSuccessful) {
                    val detectResponse = response.body()
                    if (detectResponse?.success == true) {
                        val recommendations = detectResponse.recommendations
                        val summary = detectResponse.summary

                        val action = NutritionFragmentDirections.actionNavigationNutritionToNavigationNutritionResult(
                            recommendations,
                            summary
                        )
                        findNavController().navigate(action)
                    } else {
                        val errorMessage = detectResponse?.message ?: "Gagal mendeteksi makanan"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mendeteksi makanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetectResponse>, t: Throwable) {
                Log.e("NutritionFragment", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showHelpDialog() {
        val dialog = HelpDialogFragment()
        dialog.show(childFragmentManager, "HelpDialogFragment")

        // Tunda pemanggilan requireView()
        dialog.dialog?.setOnShowListener {
            dialog.requireView().translationY = dialog.requireView().height.toFloat()
            dialog.requireView().animate()
                .translationY(0f)
                .setDuration(300L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

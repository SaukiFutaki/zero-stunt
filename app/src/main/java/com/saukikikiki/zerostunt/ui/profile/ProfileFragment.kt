package com.saukikikiki.zerostunt.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.saukikikiki.zerostunt.data.api.ApiClient
import com.saukikikiki.zerostunt.data.api.UserResponse
import com.saukikikiki.zerostunt.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val uid = "MFPybvyCVgZb3Ca3030svvxFMKx1"

        ApiClient.authService.getUser(uid).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                            Toast.makeText(requireContext(), "Berhasil mengambil data user ${userResponse.user}", Toast.LENGTH_SHORT).show()
//                       binding.tvNamaAnakValue.text = userResponse.user.name
//                        binding.tvEmailValue.text = userResponse.user.email

                    } else {
                        val errorMessage = userResponse?.message ?: "Gagal mengambil data user"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mengambil data user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ProfileFragment", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
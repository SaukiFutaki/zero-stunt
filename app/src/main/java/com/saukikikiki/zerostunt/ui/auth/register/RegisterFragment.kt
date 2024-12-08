package com.saukikikiki.zerostunt.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentRegisterBinding
import com.saukikikiki.zerostunt.ui.api.ApiClient
import com.saukikikiki.zerostunt.ui.api.LoginResponse
import com.saukikikiki.zerostunt.ui.api.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDaftar.setOnClickListener {
            val name = binding.tilNama.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (isValidInput(name, email, password)) {
                registerUser(name, email, password)
            }
        }

        binding.tvMasuk.setOnClickListener {
            val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
            findNavController().navigate(action)
        }
    }

    private fun isValidInput(name: String, email: String, password: String): Boolean {
        return if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val registerRequest = RegisterRequest(name, email, password) // Gunakan RegisterRequest
        ApiClient.authService.register(registerRequest).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    Log.d("RegisterFragment", "Response body: $registerResponse")
                    if (registerResponse?.success == true) {
                        Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
                        findNavController().navigate(action)
                    } else {
                        Log.d("RegisterFragment", "Response message: ${registerResponse?.message}")
                        Toast.makeText(requireContext(), "Registrasi gagal: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("RegisterFragment", "Response code: ${response.code()}, error body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Registrasi gagal. Cek input Anda.", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

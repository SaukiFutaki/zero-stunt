package com.saukikikiki.zerostunt.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentLoginBinding
import com.saukikikiki.zerostunt.data.api.ApiClient
import com.saukikikiki.zerostunt.data.api.LoginRequest
import com.saukikikiki.zerostunt.data.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMasuk.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (isValidInput(email, password)) {
                loginUser(email, password)
            }
        }

        binding.tvDaftar.setOnClickListener {
            val action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister()
            findNavController().navigate(action)
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        ApiClient.authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "Login ${loginResponse.uid}! ",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigasi ke HomeFragment atau TambahDataAnakFragment
                        val sharedPrefs = requireActivity().getSharedPreferences(
                            "user_data",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPrefs.edit()
                        editor.putBoolean("is_logged_in", true)
                        editor.putString("token", loginResponse.token)
                        editor.putString("uid", loginResponse.uid)
                        editor.apply()
                        Log.d("LoginFragment", "uid: ${loginResponse.uid}")
                        val action =
                            LoginFragmentDirections.actionNavigationLoginToNavigationTambahDataAnak()
                        findNavController().navigate(action)
                    } else {
                        val errorMessage = loginResponse?.message ?: "Login gagal."
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Login gagal. Cek email dan password Anda.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Email dan password harus diisi", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
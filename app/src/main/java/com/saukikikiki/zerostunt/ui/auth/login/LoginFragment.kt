package com.saukikikiki.zerostunt.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentLoginBinding

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
                if (authenticateUser(email, password)) {

                    Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()


                    saveUserData(email, password)


                    if (isChildDataAvailable(email)) {

                        val action = LoginFragmentDirections.actionNavigationLoginToNavigationHome()
                        findNavController().navigate(action)
                    } else {

                        val action = LoginFragmentDirections.actionNavigationLoginToNavigationTambahDataAnak()
                        findNavController().navigate(action)
                    }
                } else {

                    Toast.makeText(requireContext(), "Login gagal!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvDaftar.setOnClickListener {

            val action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister()
            findNavController().navigate(action)
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun authenticateUser(email: String, password: String): Boolean {
        val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val savedEmail = sharedPrefs.getString("email", "")
        val savedPassword = sharedPrefs.getString("password", "")

        return email == savedEmail && password == savedPassword
    }

    private fun saveUserData(email: String, password: String) {
        val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun isChildDataAvailable(email: String): Boolean {
        // Logika untuk cek apakah sudah ada data anak untuk user ini
        // (misalnya, cek di SharedPreferences atau database)
        // Untuk sementara, return false agar selalu diarahkan ke TambahDataAnakFragment
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
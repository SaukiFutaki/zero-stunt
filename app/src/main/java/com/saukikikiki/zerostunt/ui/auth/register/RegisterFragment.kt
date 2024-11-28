package com.saukikikiki.zerostunt.ui.auth.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentRegisterBinding

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
            val nama = binding.tilNama.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (isValidInput(nama, email, password)) {

                saveUserData(nama, email, password)

                Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()


                val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
                findNavController().navigate(action)
            }
        }

        binding.tvMasuk.setOnClickListener {

            val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
            findNavController().navigate(action)
        }
    }

    private fun isValidInput(nama: String, email: String, password: String): Boolean {
        if (nama.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveUserData(nama: String, email: String, password: String) {
        val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("nama", nama)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
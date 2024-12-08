package com.saukikikiki.zerostunt.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentLoginBinding
import com.saukikikiki.zerostunt.ui.api.ApiClient
import com.saukikikiki.zerostunt.ui.api.LoginRequest
import com.saukikikiki.zerostunt.ui.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    //Variabel Nullable untuk mengakses elemen UI yang didefinisikan fragmentLoginBinding
    private var _binding: FragmentLoginBinding? = null
    //Non-Nullable
    private val binding get() = _binding!!

    //Inisialisasi halaman
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
        //Navigasi Ke Halaman Pendaftaran
        binding.tvDaftar.setOnClickListener {
            val action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister()
            findNavController().navigate(action)
        }
    }

    //Dari email dan password user, masuk di objek login request lalu mengirim permintaan loginnya
    //Lalu diberi callback saat berhasil dan saat gagal
    //Callback terakhir yaitu pesan saat error dimana tidak ada jaringan atau masalah server API
    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        ApiClient.authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                        val action = LoginFragmentDirections.actionNavigationLoginToNavigationHome()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(requireContext(), "Login gagal: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Login gagal. Cek email dan password Anda.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Cek email dan Password sudah terisi atau belum
    private fun isValidInput(email: String, password: String): Boolean {
        return if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
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









    /**TAHAN DULU KODE DIBAWAH INI KARENA SHARED PREFERENCE SAJA*
     *
     *
     *
     *
     *
    private fun authenticateUser(email: String, password: String) {
        //val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        //val savedEmail = sharedPrefs.getString("email", "")
        //val savedPassword = sharedPrefs.getString("password", "")

        //return email == savedEmail && password == savedPassword

        val authService = ApiClient.authService
        val loginRequest = LoginRequest(email, password)

        authService.login(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token

                    saveUserData(email, token ?: "")
                    Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()

                    if (isChildDataAvailable(email)) {
                        val action = LoginFragmentDirections.actionNavigationLoginToNavigationHome()
                        findNavController().navigate(action)
                    } else {
                        val action = LoginFragmentDirections.actionNavigationLoginToNavigationTambahDataAnak()
                        findNavController().navigate(action)
                    }
                } else {
                    Toast.makeText(requireContext(), "Login gagal: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

**/

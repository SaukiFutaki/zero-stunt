package com.saukikikiki.zerostunt.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.saukikikiki.zerostunt.R
import com.saukikikiki.zerostunt.data.api.ApiClient
import com.saukikikiki.zerostunt.data.api.UserResponse
import com.saukikikiki.zerostunt.data.room.AppDatabase
import com.saukikikiki.zerostunt.databinding.FragmentProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "child-database"
        ).build()

        val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)


        val uid = sharedPrefs.getString("uid", "") ?: ""


        CoroutineScope(Dispatchers.IO).launch {
            val childData = appDatabase.ChildDao().getLastChildData()


            requireActivity().runOnUiThread {
                if (childData != null) {
                    val child = childData
                    Log.d("ProfileFragment", "Child data: $child")
                    binding.tvNamaAnakValue.text = child.name
                    binding.tvTanggalLahirValue.text = "${child.age} bulan"
                    binding.tvBeratLahirValue.text = "${child.bodyWeight} kg"
                    binding.tvTinggiLahirValue.text = "${child.bodyLength} cm"
                    val gender = when (child.gender) {
                        1.0f -> "Laki-laki"
                        0.0f -> "Perempuan"
                        else -> "Tidak Diketahui"
                    }

                    if (gender == "Perempuan") {
                        binding.ivFotoProfil.setImageResource(R.drawable.baby_girl_icon)
                    } else {
                        binding.ivFotoProfil.setImageResource(R.drawable.baby_boy_icon)
                    }

                } else {

                    Toast.makeText(requireContext(), "Data anak tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ApiClient.authService.getUser(uid).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "Berhasil mengambil data user ${userResponse.user}",
                            Toast.LENGTH_SHORT
                        ).show()



                    } else {
                        val errorMessage = userResponse?.message ?: "Gagal mengambil data user"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil data user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("ProfileFragment", "Error: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val childData = appDatabase.ChildDao().getAllChildData()
            Log.d("ProfileFragment", "Child data: $childData")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
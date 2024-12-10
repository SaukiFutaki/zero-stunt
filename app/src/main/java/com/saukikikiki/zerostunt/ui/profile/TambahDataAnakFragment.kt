package com.saukikikiki.zerostunt.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentTambahDataAnakBinding

class TambahDataAnakFragment : Fragment() {

    private var _binding: FragmentTambahDataAnakBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahDataAnakBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ivLakiLaki.setOnClickListener {
            binding.ivLakiLaki.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_boy_icon_active)
            binding.ivPerempuan.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_girl_icon)
        }

        binding.ivPerempuan.setOnClickListener {
            binding.ivPerempuan.setImageResource(com.saukikikiki.zerostunt.R.drawable.ic_girl_active)
            binding.ivLakiLaki.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_boy_icon)
        }

        binding.btnSimpan.setOnClickListener {

            val namaAnak = binding.tilNamaAnak.editText?.text.toString()
            val tanggalLahir = binding.tilTanggalLahir.editText?.text.toString()
            val beratLahir = binding.tilBeratLahir.editText?.text.toString().toFloatOrNull() ?: 0f
            val tinggiLahir = binding.tilTinggiLahir.editText?.text.toString().toFloatOrNull() ?: 0f
            val jenisKelamin = if (binding.ivPerempuan.drawable.constantState == resources.getDrawable(com.saukikikiki.zerostunt.R.drawable.ic_girl_active).constantState) {
                "Perempuan"
            } else {
                "Laki-laki"
            }


            if (namaAnak.isBlank() || tanggalLahir.isBlank()) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            saveDataAnak(namaAnak, tanggalLahir, beratLahir, tinggiLahir, jenisKelamin)
            val action = TambahDataAnakFragmentDirections.actionNavigationTambahDataAnakToNavigationHome(
                namaAnak, tanggalLahir, beratLahir, tinggiLahir, jenisKelamin
            )
            findNavController().navigate(action)
        }
    }


    private fun saveDataAnak(namaAnak: String, tanggalLahir: String, beratLahir: Float, tinggiLahir: Float, jenisKelamin: String) {
        // Save data anak to shared preferences
        val sharedPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("namaAnak", namaAnak)
        editor.putString("tanggalLahir", tanggalLahir)
        editor.putFloat("beratLahir", beratLahir)
        editor.putFloat("tinggiLahir", tinggiLahir)
        editor.putString("jenisKelamin", jenisKelamin)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
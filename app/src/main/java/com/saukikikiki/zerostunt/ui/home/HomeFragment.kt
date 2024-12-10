package com.saukikikiki.zerostunt.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.R
import com.saukikikiki.zerostunt.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val namaAnak = arguments?.getString("namaAnak") ?: ""
        val jenisKelamin = arguments?.getString("jenisKelamin") ?: ""
        val tanggalLahir = arguments?.getString("tanggalLahir") ?: ""
        val beratLahir = arguments?.getFloat("beratLahir") ?: 0f
        val tinggiLahir = arguments?.getInt("tinggiLahir") ?: 0

        binding.ivProfile.setImageResource(
            if (jenisKelamin == "Perempuan") {
                R.drawable.profile_baby_girl
            } else {
                R.drawable.baby_boy_icon
            }
        )

        binding.ivProfile.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationTambahDataAnak()
            findNavController().navigate(action)
        }

        // get user_data shared preferences


        binding.tvNama.text = namaAnak
        val statusStunting = if (beratLahir < 2.5 || tinggiLahir < 48) {
            "Anak anda berpotensi\nStunting!"
        } else {
            "Anak anda TIDAK\nberpotensi Stunting!"
        }
        binding.tvStatusStunting.text = statusStunting
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
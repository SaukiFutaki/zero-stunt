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
import androidx.navigation.NavOptions
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

        val sharedPref = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val jenisKelamin = sharedPref.getString("jenisKelamin", "")
        val namaAnak = sharedPref.getString("namaAnak", "")



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

        binding.ivNotification.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationNotifications()
            findNavController().navigate(action, NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build())
        }

        binding.tvNama.text = namaAnak

        val statusStunting = sharedPref.getString("statusStunting", "Belum Ada Data, Silahkan Isi Ya!")
        binding.tvStatusStunting.text = "Status: $statusStunting"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


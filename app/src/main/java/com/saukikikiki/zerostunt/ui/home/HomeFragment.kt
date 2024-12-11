package com.saukikikiki.zerostunt.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.saukikikiki.zerostunt.R
import com.saukikikiki.zerostunt.data.room.AppDatabase
import com.saukikikiki.zerostunt.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var appDatabase: AppDatabase

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

        appDatabase = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "child-database"
        ).build()




        CoroutineScope(Dispatchers.IO).launch {
            val childData = appDatabase.ChildDao().getLastChildData()


            requireActivity().runOnUiThread {
                if (childData != null) {
                    val child = childData
                    Log.d("ProfileFragment", "Child data: $child")
                    binding.tvNama.text = child.name
                    binding.tvStatusStunting.text = "Status: ${child.stuntingStatus}"


                    val gender = when (child.gender) {
                        1.0f -> "Laki-laki"
                        0.0f -> "Perempuan"
                        else -> "Tidak Diketahui"
                    }

                    if (gender == "Perempuan") {
                        binding.ivProfile.setImageResource(R.drawable.baby_girl_icon)
                    } else {
                        binding.ivProfile.setImageResource(R.drawable.baby_boy_icon)
                    }


                } else {

                    Toast.makeText(requireContext(), "Data anak tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }



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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


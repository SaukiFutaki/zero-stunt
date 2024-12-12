package com.saukikikiki.zerostunt.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        setupMissionObservers()


        setupChildData()
        setupClickListeners()
    }

    private fun setupMissionObservers() {

        homeViewModel.misi1.observe(viewLifecycleOwner) { mission ->
            updateMissionUI(binding.cbMisi1, binding.llMisi1, mission)
        }

        homeViewModel.misi2.observe(viewLifecycleOwner) { mission ->
            updateMissionUI(binding.cbMisi2, binding.llMisi2, mission)
        }

        homeViewModel.misi3.observe(viewLifecycleOwner) { mission ->
            updateMissionUI(binding.cbMisi3, binding.llMisi3, mission)
        }


        homeViewModel.misiBaru.observe(viewLifecycleOwner) { newMissions ->
            if (newMissions.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Selamat! Anda telah menyelesaikan semua misi",
                    Toast.LENGTH_SHORT
                ).show()


                updateNewMissionsUI(newMissions)
            }
        }
    }

    private fun updateMissionUI(checkBox: View, layout: View, mission: Mission) {

        if (checkBox is CheckBox) {
            checkBox.isChecked = mission.isFinished
        }


        if (mission.isFinished) {
            layout.animate()
                .translationXBy(layout.width.toFloat())
                .alpha(0f)
                .setDuration(300L)
                .withEndAction {
                    layout.visibility = View.GONE
                }
                .start()
        } else {
            layout.visibility = View.VISIBLE
            layout.translationX = 0f
            layout.alpha = 1f
        }
    }
    private fun updateNewMissionsUI(newMissions: List<Mission>) {
        // Pastikan container kosong sebelum menambahkan misi baru
        binding.missionContainer.removeAllViews()

        // Sembunyikan 3 misi lama
        binding.llMisi1.visibility = View.GONE
        binding.llMisi2.visibility = View.GONE
        binding.llMisi3.visibility = View.GONE

        // Tambahkan misi baru ke layout
        newMissions.forEachIndexed { index, mission ->
            val missionLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(16, 16, 16, 16)
                gravity = Gravity.CENTER_VERTICAL
            }

            // Tambah ImageView
            val imageView = ImageView(requireContext()).apply {
                setImageResource(R.drawable.figma_ic_latihan_mendangak)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Tambah TextView untuk deskripsi misi
            val textView = TextView(requireContext()).apply {
                text = "${mission.title}\n${mission.description}"
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    leftMargin = 16
                }
            }

            // Tambah CheckBox
            val checkBox = CheckBox(requireContext()).apply {
                isChecked = mission.isFinished
                setOnCheckedChangeListener { _, isChecked ->
                    homeViewModel.updateMissionStatus(mission.id, isChecked)
                }
            }

            // Susun komponen ke dalam layout
            missionLayout.addView(imageView)
            missionLayout.addView(textView)
            missionLayout.addView(checkBox)

            // Tambahkan layout misi baru ke container
            binding.missionContainer.addView(missionLayout)
        }
    }

    private fun setupChildData() {
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

                    binding.ivProfile.setImageResource(
                        if (gender == "Perempuan") R.drawable.baby_girl_icon
                        else R.drawable.baby_boy_icon
                    )
                } else {
                    Toast.makeText(requireContext(), "Data anak tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Profile click listener
        binding.ivProfile.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationTambahDataAnak()
            findNavController().navigate(action)
        }

        // Notification click listener
        binding.ivNotification.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationNotifications()
            findNavController().navigate(action, NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build())
        }

        // Mission checkbox listeners
        binding.cbMisi1.setOnCheckedChangeListener { _, isChecked ->
            homeViewModel.updateMissionStatus(1, isChecked)
        }

        binding.cbMisi2.setOnCheckedChangeListener { _, isChecked ->
            homeViewModel.updateMissionStatus(2, isChecked)
        }

        binding.cbMisi3.setOnCheckedChangeListener { _, isChecked ->
            homeViewModel.updateMissionStatus(3, isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
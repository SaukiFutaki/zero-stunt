package com.saukikikiki.zerostunt.ui.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.saukikikiki.zerostunt.databinding.FragmentNotificationsBinding
import com.saukikikiki.zerostunt.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        // Cek izin dan jadwalkan notifikasi mingguan
        if (isNotificationPermissionGranted()) {
            scheduleWeeklyNotification()
        } else {
            requestNotificationPermission()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
            // Anda dapat menampilkan UI yang memberi tahu pengguna mengapa izin diperlukan
        }
        // Meminta izin untuk notifikasi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATION_PERMISSION
            )
        }
    }

    private fun scheduleWeeklyNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(10, TimeUnit.SECONDS)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()


        Log.d("NotificationsFragment", "Scheduling Weekly Notification")

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "WeeklyNotification",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        // Cek status pekerjaan yang dijadwalkan
        val workStatus = WorkManager.getInstance(requireContext())
            .getWorkInfoByIdLiveData(workRequest.id)
        workStatus.observe(viewLifecycleOwner) { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.RUNNING) {
                Log.d("NotificationsFragment", "Notification worker is running")
            } else if (workInfo != null) {
                Log.d("NotificationsFragment", "Notification worker state: ${workInfo.state}")
            }
        }
    }


    // Menangani hasil permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, jalankan notifikasi
                scheduleWeeklyNotification()
            } else {
                // Izin ditolak, beri tahu pengguna bahwa izin diperlukan untuk notifikasi
                // Misalnya, tampilkan pesan atau batalkan pekerjaan
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1
    }
}
package com.saukikikiki.zerostunt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.saukikikiki.zerostunt.databinding.ActivityMainBinding
import com.saukikikiki.zerostunt.ui.auth.login.LoginFragmentDirections
import com.saukikikiki.zerostunt.ui.auth.register.RegisterFragmentDirections
import com.saukikikiki.zerostunt.ui.home.HomeFragmentDirections
import com.saukikikiki.zerostunt.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val openFragment = intent.getStringExtra("openFragment")
        if (openFragment == "TambahDataAnakFragment") {
            navController.navigate(R.id.navigation_tambah_data_anak)
        }

        // visibility of bottom navigation view at login and register fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login,
                R.id.navigation_register, R.id.navigation_nutrition_result, R.id.navigation_scan_result, R.id.navigation_tambah_data_anak, R.id.navigation_notifications -> {
                    binding.navView.visibility = View.GONE
                }

                else -> {
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }

        navView.setupWithNavController(navController)
        when {
            isUserLoggedIn() -> {
                // User sudah login, cek apakah sudah ada data anak
                when {
                    isChildDataAvailable() -> {
                        // Sudah ada data anak, arahkan ke HomeFragment
                        navController.navigate(R.id.navigation_home)
                    }

                    else -> {
                        // Belum ada data anak, arahkan ke TambahDataAnakFragment
                        navController.navigate(R.id.navigation_tambah_data_anak)
                    }
                }
            }

            else -> {
                // User belum login, ke RegisterFragment
                navController.navigate(R.id.navigation_register)
            }
        }


        if (isNotificationPermissionGranted()) {
            scheduleWeeklyNotification()
        }

    }

    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isUserLoggedIn(): Boolean {

        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val uid = sharedPrefs.getString("uid", null)
        if (uid != null) {
            Log.d("MainActivity", "UID: $uid") // Uid ada
        } else {
            Log.d("MainActivity", "UID tidak tersedia") // Uid tidak ada
        }

        return sharedPrefs.getBoolean("is_logged_in", false)

    }


    private fun scheduleWeeklyNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            7, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "weekly_reminder_channel",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationRequest
        )
    }

    private fun isChildDataAvailable(): Boolean {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val statusStunting = sharedPrefs.getString("statusStunting", null)
        return statusStunting != null
    }
}
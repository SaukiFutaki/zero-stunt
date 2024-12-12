package com.saukikikiki.zerostunt

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saukikikiki.zerostunt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(2000)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Daftar fragment utama
        val mainDestinations = setOf(
            R.id.navigation_home,
            R.id.navigation_scan,
            R.id.navigation_profile,
            R.id.navigation_nutrition
        )

        // Visibility of bottom navigation view
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView.visibility = when (destination.id) {
                R.id.navigation_login,
                R.id.navigation_register,
                R.id.navigation_nutrition_result,
                R.id.navigation_scan_result,
                R.id.navigation_tambah_data_anak,
                R.id.navigation_notifications -> View.GONE
                else -> View.VISIBLE
            }
        }

        // Setup bottom navigation view with navController
        navView.setupWithNavController(navController)

        // Navigasi berdasarkan status login dan ketersediaan data anak
        when {
            isUserLoggedIn() -> {
                if (isChildDataAvailable()) {
                    navController.navigate(R.id.navigation_home)
                } else {
                    navController.navigate(R.id.navigation_tambah_data_anak)
                }
            }
            else -> {
                navController.navigate(R.id.navigation_register)
            }
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestinationId = navController.currentDestination?.id
        val mainDestinations = setOf(
            R.id.navigation_home,
            R.id.navigation_scan,
            R.id.navigation_profile,
            R.id.navigation_nutrition
        )

        when {
            currentDestinationId == R.id.navigation_home -> {
                // Jika di fragment home, keluar aplikasi
                finish()
            }
            currentDestinationId in setOf(
                R.id.navigation_profile,
                R.id.navigation_scan,
                R.id.navigation_nutrition
            ) -> {
                // Jika di fragment profile, scan, atau nutrition, kembali ke home
                navController.navigate(R.id.navigation_home)
            }
            else -> {
                // Kembali ke fragment sebelumnya
                super.onBackPressed()
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val uid = sharedPrefs.getString("uid", null)
        if (uid != null) {
            Log.d("MainActivity", "UID: $uid") // UID tersedia
        } else {
            Log.d("MainActivity", "UID tidak tersedia") // UID tidak ada
        }
        return sharedPrefs.getBoolean("is_logged_in", false)
    }

    private fun isChildDataAvailable(): Boolean {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val statusStunting = sharedPrefs.getString("statusStunting", null)
        return statusStunting != null
    }
}

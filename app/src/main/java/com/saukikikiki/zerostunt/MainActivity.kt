package com.saukikikiki.zerostunt

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.saukikikiki.zerostunt.databinding.ActivityMainBinding
import com.saukikikiki.zerostunt.ui.auth.login.LoginFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home,
//             R.id.navigation_notifications,
//                R.id.navigation_nutrition,
//                R.id.navigation_scan,
//                R.id.navigation_profile,
//                R.id.navigation_tambah_data_anak
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        if (isUserLoggedIn()) {
            Toast.makeText(this, "User logged in", Toast.LENGTH_SHORT).show()
        } else {
           Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()

        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("is_logged_in", false)
    }
}
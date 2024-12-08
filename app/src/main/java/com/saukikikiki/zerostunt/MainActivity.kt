package com.saukikikiki.zerostunt

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.saukikikiki.zerostunt.databinding.ActivityMainBinding
import com.saukikikiki.zerostunt.ui.auth.login.LoginFragmentDirections
import com.saukikikiki.zerostunt.ui.auth.register.RegisterFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // visibility of bottom navigation view at login and register fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login,
                    R.id.navigation_register,R.id.navigation_nutrition_result -> {
                binding.navView.visibility = View.GONE
            }
                else -> {
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }

        navView.setupWithNavController(navController)

        if (isUserLoggedIn()) {
            Toast.makeText(this, "User logged in", Toast.LENGTH_SHORT).show()
            val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationHome()
            navController.navigate(action)
        } else {
           Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            val action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister()
            navController.navigate(action)

        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("is_logged_in", false)
    }
}
package com.example.spaceshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.spaceshare.manager.SharedPreferencesManager
import com.example.spaceshare.ui.view.ListingFragment
import com.example.spaceshare.ui.view.ProfileFragment
import com.example.spaceshare.ui.view.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    companion object {
        private val TAG = this::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesManager.init(this)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    navigateToFragment(SearchFragment())
                    true
                }
                R.id.action_listings -> {
                    navigateToFragment(ListingFragment())
                    true
                }
                R.id.action_profile -> {
                    navigateToFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = if (SharedPreferencesManager.isHostMode())
            R.id.action_listings else
            R.id.action_search
        SharedPreferencesManager.registerListener(::updateUI)
        updateUI(SharedPreferencesManager.isHostMode())
    }

    private fun updateUI(isHostMode: Boolean) {
        val menu = bottomNavigationView.menu
        // Client-specific tabs
        val searchTab = menu.findItem(R.id.action_search)
        // Host-specific tabs
        val listingsTab = menu.findItem(R.id.action_listings)
        if (isHostMode) {
            searchTab.isVisible = false
            listingsTab.isVisible = true
        } else {
            searchTab.isVisible = true
            listingsTab.isVisible = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_nav_host_fragment, fragment)
            .commit()
    }
}
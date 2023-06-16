package com.example.spaceshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.spaceshare.manager.SharedPreferencesManager
import com.example.spaceshare.ui.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    @Inject
    lateinit var mainViewModel: MainViewModel

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
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        // UI listeners
        NavigationUI.setupWithNavController(bottomNavigationView, navController, false)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val currentDestination = navController.currentDestination?.id
            val selectedDestination = menuItem.itemId

            if (currentDestination != selectedDestination) {
                navController.popBackStack()
                navController.navigate(selectedDestination)
            }

            true
        }
        bottomNavigationView.selectedItemId = if (SharedPreferencesManager.isHostMode())
            R.id.listingFragment else
            R.id.searchFragment
        SharedPreferencesManager.isHostMode.observe(this) { isHostMode ->
            mainViewModel.setIsHostMode(isHostMode)
        }
        mainViewModel.isHostModeLiveData.observe(this) { isHostMode ->
            updateUI(isHostMode)
        }

        updateUI(SharedPreferencesManager.isHostMode())
    }

    private fun updateUI(isHostMode: Boolean) {
        val menu = bottomNavigationView.menu
        menu.findItem(R.id.listingFragment).isVisible = isHostMode
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != navController.graph.startDestinationId) {
            val isCurrentDestinationMenu = when (navController.currentDestination?.id) {
                R.id.searchFragment, R.id.listingFragment, R.id.profileFragment -> true
                else -> false
            }
            if (isCurrentDestinationMenu) {
                navController.navigate(navController.graph.startDestinationId)
            } else {
                navController.popBackStack()
            }
        } else {
            finish()
        }
    }
}
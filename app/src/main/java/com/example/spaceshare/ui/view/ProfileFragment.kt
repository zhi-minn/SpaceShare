package com.example.spaceshare.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.SplashActivity
import com.example.spaceshare.databinding.FragmentProfileBinding
import com.example.spaceshare.manager.SharedPreferencesManager
import com.example.spaceshare.ui.viewmodel.AuthViewModel
import com.example.spaceshare.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        // UI Setup
        configureUI()
        configureButtons()
    }

    private fun configureUI() {
        mainViewModel.isHostModeLiveData.observe(viewLifecycleOwner) { isHostMode ->
            updateUI(isHostMode)
        }
        updateUI(SharedPreferencesManager.isHostMode())
    }

    private fun configureButtons() {
        // Switching modes
        binding.btnSwitchMode.setOnClickListener {
            SharedPreferencesManager.switchMode()
            if (SharedPreferencesManager.isHostMode()) {
                navController.navigate(R.id.action_profileFragment_to_listingFragment)
            } else {
                navController.navigate(R.id.action_profileFragment_to_searchFragment)
            }
        }

        // Preferences
        binding.btnPreferences.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_preferencesFragment)
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateUI(isHostMode: Boolean) {
        binding.btnSwitchMode.text = if (isHostMode)
            "Switch to client" else
            "Switch to host"
        binding.uiMode.text = if (isHostMode)
            "You are in host mode" else
            "You are in client mode"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.isHostModeLiveData.removeObservers(viewLifecycleOwner)
    }
}
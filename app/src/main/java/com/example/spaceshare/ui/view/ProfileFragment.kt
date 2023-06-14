package com.example.spaceshare.ui.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentProfileBinding
import com.example.spaceshare.manager.SharedPreferencesManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesListener = SharedPreferencesManager.registerListener(::updateUI)
        updateUI(SharedPreferencesManager.isHostMode())
        binding.btnSwitchMode.setOnClickListener {
            SharedPreferencesManager.switchMode()
        }
    }

    private fun updateUI(isHostMode: Boolean) {
        binding.btnSwitchMode.text = if (isHostMode)
            "Switch to client" else
            "Switch to host"
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesManager.unregisterListener(sharedPreferencesListener)
    }
}
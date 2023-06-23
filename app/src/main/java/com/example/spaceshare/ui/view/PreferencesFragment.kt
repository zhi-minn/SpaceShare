package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentPreferencesBinding
import com.example.spaceshare.ui.viewmodel.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment : Fragment() {

    private lateinit var binding: FragmentPreferencesBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var viewModel: PreferencesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureButtons()
        configureObservers()
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btn1km.setOnClickListener {
            viewModel.setRadius(1)
        }

        binding.btn5km.setOnClickListener {
            viewModel.setRadius(5)
        }

        binding.btn10km.setOnClickListener {
            viewModel.setRadius(10)
        }

        binding.btnSave.setOnClickListener {
            viewModel.updatePreferences()
            navController.popBackStack()
        }
    }

    private fun configureObservers() {
        viewModel.preferencesLiveData.observe(viewLifecycleOwner) { preferences ->
            preferences?.let {
                val radius = preferences.radius
                binding.btn1km.isSelected = radius == 1
                binding.btn5km.isSelected = radius == 5
                binding.btn10km.isSelected = radius == 10
            }
        }
    }
}
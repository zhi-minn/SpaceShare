package com.example.spaceshare.ui.view

import MapDialogFragment
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
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
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
        // Set container to invisible first while data loading
        binding.container.visibility = View.GONE
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

        binding.location.setOnClickListener {
            val mapDialogFragment = MapDialogFragment(viewModel, null)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
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
        viewModel.preferencesLoaded.observe(viewLifecycleOwner) { isLoaded ->
            if (isLoaded) {
             binding.progressBar.visibility = View.GONE
             binding.container.visibility = View.VISIBLE
            } else {
             binding.progressBar.visibility = View.VISIBLE
             binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.preferencesLiveData.observe(viewLifecycleOwner) { preferences ->
            preferences?.let {
                // Location
                val location = preferences.location
                if (location != null) {
                    binding.location.text = GeocoderUtil.getAddress(location.latitude, location.longitude)
                }

                // Radius
                val radius = preferences.radius
                binding.btn1km.isSelected = radius == 1
                binding.btn5km.isSelected = radius == 5
                binding.btn10km.isSelected = radius == 10
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) viewModel.fetchPreferences(userId)
    }
}
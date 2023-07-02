package com.example.spaceshare.ui.view

import MapDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogPreferencesBinding
import com.example.spaceshare.ui.viewmodel.PreferencesViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.material.slider.Slider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPreferencesBinding
    @Inject
    lateinit var viewModel: PreferencesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_preferences, container, false)
        // Set container to invisible first while data loading
        binding.container.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureButtons()
        configureObservers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SlideLeftDialogStyle)
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }

        binding.location.setOnClickListener {
            val mapDialogFragment = MapDialogFragment(viewModel, null)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
        }

        binding.searchRadiusSlider.setLabelFormatter { value ->
            "$value km"
        }
        binding.searchRadiusSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            viewModel.setRadius(value.toInt())
        })

        binding.switchEmailUpdates.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsActive(isChecked)
        }

        binding.btnSave.setOnClickListener {
            viewModel.updatePreferences()
            this.dismiss()
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

                binding.searchRadiusSlider.value = preferences.radius.toFloat()
                binding.distanceIndicator.text = "${preferences.radius} km"
                binding.switchEmailUpdates.isChecked = preferences.isActive
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) viewModel.fetchPreferences(userId)
    }
}
package com.example.spaceshare.ui.view

import MapDialogFragment
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogSearchBinding
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.Slider
import java.util.Objects

class SearchDialogFragment(
    private val searchViewModel: SearchViewModel
) : DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: DialogSearchBinding
    private lateinit var navController: NavController

    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)
        geocoder = Geocoder(requireContext())

        configureCards()
        configureButtons()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SearchAndFilterDialogStyle)
    }

    private fun configureCards() {
        // Where
        binding.whereCard.setOnClickListener {
            hideWhatSelectorCard()
            val mapDialogFragment = MapDialogFragment(searchViewModel, null)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
        }
        searchViewModel.location.observe(viewLifecycleOwner) { location ->
            val address = GeocoderUtil.getAddress(location.latitude, location.longitude)
            if (address != "") {
                binding.searchLocation.text = address
                // Make radius related UI disappear
                binding.searchRadiusSlider.isGone = false
                binding.seachRadiusTextView.isGone = false
                binding.searchRadiusIndicator.isGone = false
            }
            else {
                binding.searchLocation.text = "Anywhere"
                // Make radius related UI appear
                binding.searchRadiusSlider.isGone = true
                binding.seachRadiusTextView.isGone = true
                binding.searchRadiusIndicator.isGone = true
            }
        }
        // Search Radius
        binding.searchRadiusSlider.setLabelFormatter { value ->
            "$value km"
        }
        binding.searchRadiusSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                searchViewModel.setSearchRadius(binding.searchRadiusSlider.value)
            }
        })
        searchViewModel.searchRadius.observe(viewLifecycleOwner) { radius ->
            binding.searchRadiusSlider.value = radius
            binding.searchRadiusIndicator.text = "$radius km"
        }

        // When
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
        dateRangePicker.addOnPositiveButtonClickListener {
            binding.searchTime.text = dateRangePicker.headerText

            val startTime = dateRangePicker.selection?.first
            val endTime = dateRangePicker.selection?.second
            if (startTime != null && endTime != null) {
                searchViewModel.setStartTime(startTime)
                searchViewModel.setEndTime(endTime)
            }
        }
        binding.whenCard.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, TAG)
            hideWhatSelectorCard()
        }
        searchViewModel.endTime.observe(viewLifecycleOwner) {
            if (it.equals(0))
                binding.searchTime.text = "Anytime"
        }

        // What
        binding.whatCard.setOnClickListener {
            it.isGone = true
            binding.whatSelectorCard.isGone = false
        }
        binding.whatSelectorAddSize.setOnClickListener {
            searchViewModel.incrementSpaceRequired()
        }
        binding.whatSelectorMinusSize.setOnClickListener {
            searchViewModel.decrementSpaceRequired()
        }
        searchViewModel.spaceRequired.observe(viewLifecycleOwner) { spaceRequired ->
            binding.whatSelectorSizeText.text = spaceRequired.toString()
            if (spaceRequired == 0.0)
                binding.searchSize.text = "Anysize"
            else
                binding.searchSize.text = spaceRequired.toString() + " cubic metres"
        }
    }

    private fun configureButtons() {
        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.btnSearch.setOnClickListener {
            searchViewModel.submitSearch()
            dialog?.dismiss()
        }
    }

    private fun hideWhatSelectorCard() {
        binding.whatCard.isGone = false
        binding.whatSelectorCard.isGone = true
    }

}
package com.example.spaceshare.ui.view

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogFilterCriteriaBinding
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterDialogFragment(
    private val listingViewModel: ListingViewModel
) : DialogFragment() {

    private lateinit var binding: DialogFilterCriteriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter_criteria, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureSliders()
        configureFilters()
        configureButtons()
        configureListeners()
    }

    private fun configureSliders() {
        val decimalFormat = DecimalFormat("0.0") // Set the desired decimal format
        binding.spaceRangeSlider.setLabelFormatter { value -> decimalFormat.format(value) }
    }

    private fun configureFilters() {
        val criteria = listingViewModel.getCriteria()
        binding.switchActiveListings.isChecked = criteria.isActive
        binding.switchInactiveListings.isChecked = criteria.isInactive

        // Set range slider bounds based on min and max values from listing
        val prices = listingViewModel.listingsLiveData.value?.map { it.price }
        var maxPrice = prices?.maxOrNull()?.toFloat() ?: 100.0f
        if (maxPrice == 0.0f) maxPrice = 100.0f
        val criteriaMaxPrice = if (criteria.maxPrice > maxPrice) maxPrice else criteria.maxPrice
        binding.priceRangeSlider.valueFrom = 0.0f
        binding.priceRangeSlider.valueTo = maxPrice
        binding.priceRangeSlider.values = listOf(criteria.minPrice, criteriaMaxPrice)
        binding.priceIndicator.text = "$${String.format("%.2f", criteria.minPrice)} - $${String.format("%.2f", maxPrice)}"

        val spaces = listingViewModel.listingsLiveData.value?.map { it.spaceAvailable }
        var maxSpace = spaces?.maxOrNull()?.toFloat() ?: 10.0f
        if (maxSpace == 0.0f) maxSpace = 10.0f
        val criteriaMaxSpace = if (criteria.maxSpace > maxSpace) maxSpace else criteria.maxSpace
        binding.spaceRangeSlider.valueFrom = 0.0f
        binding.spaceRangeSlider.valueTo = maxSpace
        binding.spaceRangeSlider.values = listOf(criteria.minSpace, criteriaMaxSpace)
        binding.spaceIndicator.text = "${String.format("%.1f", criteria.minSpace)} - ${String.format("%.1f", maxSpace)}"
    }

    private fun configureButtons() {
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }

        binding.btnApply.setOnClickListener {
            val priceRange = binding.priceRangeSlider.values
            val spaceRange = binding.spaceRangeSlider.values
            val criteria = FilterCriteria(binding.switchActiveListings.isChecked, binding.switchInactiveListings.isChecked,
            priceRange[0], priceRange[1], spaceRange[0], spaceRange[1])
            listingViewModel.setCriteria(criteria)
            this.dismiss()
        }
    }

    private fun configureListeners() {
        binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val minPrice = slider.values[0]
            val maxPrice = slider.values[1]
            binding.priceIndicator.text = "$${String.format("%.2f", minPrice)} - $${String.format("%.2f", maxPrice)}"
        }

        binding.spaceRangeSlider.addOnChangeListener { slider, _, _ ->
            val minSpace = slider.values[0]
            val maxSpace = slider.values[1]
            binding.spaceIndicator.text = "${String.format("%.1f", minSpace)} - $${String.format("%.1f", maxSpace)}"
        }
    }
}
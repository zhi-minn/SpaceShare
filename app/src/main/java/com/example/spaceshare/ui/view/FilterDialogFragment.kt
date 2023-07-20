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

        var hasFilterBeenApplied = listingViewModel.hasFilterBeenApplied.value
        if (hasFilterBeenApplied == null) {
            hasFilterBeenApplied = false
        }

        // Set range slider bounds based on min and max values from search results
        // Also set range slider initial values based on previous setting
        var maxPrice = listingViewModel.getListingsMaxPrice()

        var criteriaMaxPrice = maxPrice
        if (hasFilterBeenApplied) {
            criteriaMaxPrice = criteria.maxPrice.toDouble()
        }

        binding.priceRangeSlider.valueFrom = 0.0f
        binding.priceRangeSlider.valueTo = maxPrice.toFloat()
        binding.priceRangeSlider.values = listOf(criteria.minPrice, criteriaMaxPrice.toFloat())
        setPriceSliderText(criteria.minPrice, criteriaMaxPrice.toFloat())


        var maxSpace = listingViewModel.getListingsMaxSpace()

        var criteriaMaxSpace = maxSpace
        if (hasFilterBeenApplied) {
            criteriaMaxSpace = criteria.maxSpace.toDouble()
        }

        binding.spaceRangeSlider.valueFrom = 0.0f
        binding.spaceRangeSlider.valueTo = maxSpace.toFloat()
        binding.spaceRangeSlider.values = listOf(criteria.minSpace, criteriaMaxSpace.toFloat())
        setSpaceSliderText(criteria.minSpace, maxSpace.toFloat())
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
            listingViewModel.setHasFilterBeenApplied(true)
            this.dismiss()
        }
    }

    private fun configureListeners() {
        binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val minPrice = slider.values[0]
            val maxPrice = slider.values[1]
            setPriceSliderText(minPrice, maxPrice)
        }

        binding.spaceRangeSlider.addOnChangeListener { slider, _, _ ->
            val minSpace = slider.values[0]
            val maxSpace = slider.values[1]
            setSpaceSliderText(minSpace, maxSpace)
        }
    }

    private fun setPriceSliderText(minPrice: Float, maxPrice: Float) {
        binding.priceIndicator.text =
            "$${String.format("%.2f", minPrice)} - $${String.format("%.2f", maxPrice)}"
    }

    private fun setSpaceSliderText(minSpace: Float, maxSpace: Float) {
        binding.spaceIndicator.text =
            "${String.format("%.1f", minSpace)} - ${String.format("%.1f", maxSpace)} m\u00B3"
    }
}
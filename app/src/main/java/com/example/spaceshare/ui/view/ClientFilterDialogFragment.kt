package com.example.spaceshare.ui.view

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogClientFilterBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.enums.FilterSortByOption
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFilterDialogFragment() : DialogFragment() {

    private lateinit var binding: DialogClientFilterBinding

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SearchAndFilterDialogStyle)

        searchViewModel = (requireParentFragment() as SearchFragment).searchViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_client_filter, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureSliders()
        configureAmenities()
        configureButtons()
        configureRadioButtons()
    }


    private fun configureSliders() {
        val criteria = searchViewModel.filterCriteria.value
        if (criteria != null) {
            var hasFilterBeenApplied = searchViewModel.hasFilterBeenApplied.value
            if (hasFilterBeenApplied == null) {
                hasFilterBeenApplied = false
            }

            // Set range slider bounds based on min and max values from search results
            // Also set range slider initial values based on previous setting
            var maxPrice = searchViewModel.getSearchResultsMaxPrice()

            var criteriaMaxPrice = maxPrice
            if (hasFilterBeenApplied) {
                criteriaMaxPrice = criteria.maxPrice.toDouble()
            }

            binding.priceRangeSlider.valueFrom = 0.0f
            binding.priceRangeSlider.valueTo = maxPrice.toFloat()
            binding.priceRangeSlider.values = listOf(criteria.minPrice, criteriaMaxPrice.toFloat())
            setPriceSliderText(criteria.minPrice, criteriaMaxPrice.toFloat())


            var maxSpace = searchViewModel.getSearchResultsMaxSpace()

            var criteriaMaxSpace = maxSpace
            if (hasFilterBeenApplied) {
                criteriaMaxSpace = criteria.maxSpace.toDouble()
            }

            binding.spaceRangeSlider.valueFrom = 0.0f
            binding.spaceRangeSlider.valueTo = maxSpace.toFloat()
            binding.spaceRangeSlider.values = listOf(criteria.minSpace, criteriaMaxSpace.toFloat())
            setSpaceSliderText(criteria.minSpace, maxSpace.toFloat())
        }

        val decimalFormat = DecimalFormat("0.0") // Set the desired decimal format
        binding.spaceRangeSlider.setLabelFormatter { value -> decimalFormat.format(value) }

        // Set slider listeners
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

    private fun configureAmenities() {
        val amenities = Amenity.values()
            .filter { searchViewModel.filterCriteria.value?.amenities!!.contains(it) }
        for (amenity in amenities) {
            when (amenity) {
                Amenity.SURVEILLANCE -> binding.surveillance.isChecked = true
                Amenity.CLIMATE_CONTROLLED -> binding.climateControlled.isChecked = true
                Amenity.WELL_LIT -> binding.lighting.isChecked = true
                Amenity.ACCESSIBILITY -> binding.accessibility.isChecked = true
                Amenity.WEEKLY_CLEANING -> binding.cleanliness.isChecked = true
            }
        }
    }

    private fun configureButtons() {
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }

        binding.btnApply.setOnClickListener {
            val priceRange = binding.priceRangeSlider.values
            val spaceRange = binding.spaceRangeSlider.values
            val amenitiesSelected: MutableList<Amenity> = getCheckedAmenities()
            val criteria = FilterCriteria(
                isActive = true, isInactive = false,
                priceRange[0], priceRange[1], spaceRange[0], spaceRange[1], amenitiesSelected
            )
            searchViewModel.setFilterCriteria(criteria)
            searchViewModel.filterByFilterCriteria()

            this.dismiss()
        }
    }

    private fun configureRadioButtons() {
        searchViewModel.sortByOption.observe(viewLifecycleOwner) { sortByOption ->
            when (sortByOption) {
                FilterSortByOption.CLOSEST ->
                    binding.radioClosest.isChecked = true
                FilterSortByOption.NEWEST ->
                    binding.radioNewest.isChecked = true
                FilterSortByOption.OLDEST ->
                    binding.radioOldest.isChecked = true
                FilterSortByOption.CHEAPEST ->
                    binding.radioCheapest.isChecked = true
                FilterSortByOption.MOST_EXPENSIVE ->
                    binding.radioMostExpensive.isChecked = true
                FilterSortByOption.LARGEST ->
                    binding.radioLargest.isChecked = true
                FilterSortByOption.SMALLEST ->
                    binding.radioSmallest.isChecked = true
            }
        }

        binding.sortByRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            onRadioButtonClicked(checkedId)
        }
    }

    private fun onRadioButtonClicked(checkedId: Int) {
        // Check which radio button was clicked
        when (checkedId) {
            R.id.radioClosest ->
                searchViewModel.setSortByOption(FilterSortByOption.CLOSEST)

            R.id.radioNewest ->
                searchViewModel.setSortByOption(FilterSortByOption.NEWEST)

            R.id.radioOldest ->
                searchViewModel.setSortByOption(FilterSortByOption.OLDEST)

            R.id.radioCheapest ->
                searchViewModel.setSortByOption(FilterSortByOption.CHEAPEST)

            R.id.radioMostExpensive ->
                searchViewModel.setSortByOption(FilterSortByOption.MOST_EXPENSIVE)

            R.id.radioLargest ->
                searchViewModel.setSortByOption(FilterSortByOption.LARGEST)

            R.id.radioSmallest ->
                searchViewModel.setSortByOption(FilterSortByOption.SMALLEST)
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

    private fun getCheckedAmenities(): MutableList<Amenity> {
        val amenities = mutableListOf<Amenity>()
        if (binding.surveillance.isChecked) amenities.add(Amenity.SURVEILLANCE)
        if (binding.climateControlled.isChecked) amenities.add(Amenity.CLIMATE_CONTROLLED)
        if (binding.lighting.isChecked) amenities.add(Amenity.WELL_LIT)
        if (binding.accessibility.isChecked) amenities.add(Amenity.ACCESSIBILITY)
        if (binding.cleanliness.isChecked) amenities.add(Amenity.WEEKLY_CLEANING)
        return amenities
    }
}
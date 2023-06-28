package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogHostListingBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.utils.GeocoderUtil

class HostListingDialogFragment(
    private val listing: Listing,
    private val listingViewModel: ListingViewModel
): DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: DialogHostListingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_host_listing, container, false)

        configureBindings()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ListingDialogStyle)
    }

    private fun configureBindings() {
        binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos)
        binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
        binding.titleText.text = listing.title
        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getGeneralLocation(location.latitude, location.longitude)
        }
        binding.price.text = getString(R.string.listing_price_template, listing.price)
        binding.description.text = listing.description
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }
    }
}
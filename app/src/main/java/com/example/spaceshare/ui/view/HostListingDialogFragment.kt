package com.example.spaceshare.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogHostListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HostListingDialogFragment(
    private val listing: Listing,
    private val listingViewModel: ListingViewModel
): DialogFragment(), OnMapReadyCallback {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: DialogHostListingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_host_listing, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

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

        // If no amenities, we do not require a divider for this section
        if (listing.amenities.isEmpty()) {
            binding.divider1.visibility = View.GONE
        }
        // Filter amenities that are not present
        val absentAmenities = Amenity.values().filterNot { listing.amenities.contains(it) }
        for (amenity in absentAmenities) {
            when (amenity) {
                Amenity.SURVEILLANCE -> binding.surveillance.visibility = View.GONE
                Amenity.CLIMATE_CONTROLLED -> binding.climateControlled.visibility = View.GONE
                Amenity.WELL_LIT-> binding.lighting.visibility = View.GONE
                Amenity.ACCESSIBILITY -> binding.accessibility.visibility = View.GONE
                Amenity.WEEKLY_CLEANING -> binding.cleanliness.visibility = View.GONE
            }
        }

        binding.description.text = listing.description
        binding.mapView.getMapAsync { map ->
            listing.location?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.isScrollGesturesEnabled = false

        // Add a marker at a specific location
        listing.location?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            map?.addMarker(MarkerOptions().position(latLng))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        map.setOnMapClickListener {
            TODO("Launch MapDialogFragment")
        }
    }
}
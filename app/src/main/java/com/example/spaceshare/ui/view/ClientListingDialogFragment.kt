package com.example.spaceshare.ui.view

import MapDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogClientListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.sql.Date
import java.util.Objects
import javax.inject.Inject

class ClientListingDialogFragment(
    private val listing: Listing
): DialogFragment(), OnMapReadyCallback {

    companion object {
        private val TAG = this::class.simpleName
    }
    private lateinit var auth: FirebaseAuth
    @Inject
    lateinit var reservationViewModel: ReservationViewModel
    @Inject
    lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: DialogClientListingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_client_listing, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        configureBindings()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
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

        binding.btnReserve.setOnClickListener {
            reserveListing()
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

            map.setOnMapClickListener {
                val mapDialogFragment = MapDialogFragment(null, latLng)
                mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
            }
        }
    }

    private fun reserveListing() {
        val hostId = listing.hostId
        val clientId = auth.currentUser?.uid
        val listingId = listing.id
        val startDate = Timestamp(Date(searchViewModel.startTime.value!!))
        val endDate = Timestamp(Date(searchViewModel.endTime.value!!))
        val unit = searchViewModel.spaceRequired.value
        val reservation =
            Reservation(hostId=hostId, clientId=clientId, listingId=listingId,
                startDate=startDate, endDate=endDate, unit=unit, status=ReservationStatus.PENDING.toInt())
        reservationViewModel.reserveListing(reservation)
    }
}
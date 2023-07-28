package com.example.spaceshare.ui.view

import MapDialogFragment
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogClientReservationBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ClientReservationDialogFragment(
    private val reservation: Reservation,
    private val listing: Listing,
    private val listener: OnReservationStatusChangedListener
) : DialogFragment(), OnMapReadyCallback {

    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var listingViewModel: ListingViewModel

    @Inject
    lateinit var reservationViewModel: ReservationViewModel

    private lateinit var binding: DialogClientReservationBinding

    interface OnReservationStatusChangedListener {
        fun onStatusChanged()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogClientReservationBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        // Get a reference to the Toolbar


        // Set the navigation icon and click listener
//        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_left)
//        toolbar.setNavigationOnClickListener { dismiss() } // dismiss the DialogFragment when the back navigation icon is pressed
//
//        // Set the title
//        toolbarTitle.text = "Reservation"

        configureBindings()
        configureButtons()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thumbsUpButton: ImageView = view.findViewById(R.id.thumbs_up)
        val thumbsDownButton: ImageView = view.findViewById(R.id.thumbs_down)

        val animScaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        val animScaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)

        var thumbsClicked = false

        thumbsUpButton.setOnClickListener {
            if (!thumbsClicked) {
                val newListing = listing.copy()
                newListing.likes += 1
                listingViewModel.updateListing(newListing)

                thumbsUpButton.startAnimation(animScaleUp)
                thumbsUpButton.startAnimation(animScaleDown)
                thumbsUpButton.setImageResource(R.drawable.like_clicked)

                // fix likes real time
                var curLikes = (listing.likes + 1).toString()
                binding.likes.text = curLikes
                thumbsClicked = true
                reservation.rated = true
                reservationViewModel.setReservationRated(reservation,true)
            }
        }

        thumbsDownButton.setOnClickListener {
            if (!thumbsClicked) {
                // Just animate the thumbsDownButton, no updates are performed
                thumbsDownButton.startAnimation(animScaleUp)
                thumbsDownButton.startAnimation(animScaleDown)
                thumbsDownButton.setImageResource(R.drawable.dislike_clicked)

                thumbsClicked = true
                reservation.rated = true
                reservationViewModel.setReservationRated(reservation,true)
            }
        }

        binding.cancelBtn.setOnClickListener {

            showConfirmCancelDialog()
        }

        binding.payBtn.setOnClickListener {
//            val itemDeclarationFragment = ItemDeclarationFragment(this)
//            itemDeclarationFragment.show(
//                Objects.requireNonNull(childFragmentManager),
//                "ItemDeclarationFragment"
//            )
            val clientPaymentDialogFragment = ClientPaymentDialogFragment(this)
        }
    }

    private fun showConfirmCancelDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Cancellation")
        builder.setMessage("Are you sure you want to cancel your booking request?")


        builder.setPositiveButton("Yes") { dialog, which ->
            // cancel request and return space
            CoroutineScope(Dispatchers.IO).launch {
                reservationViewModel.cancelSpace(
                    reservation.spaceRequested, listing,
                    reservation.startDate!!.toDate().time, reservation.endDate!!.toDate().time
                )
            }
            reservationViewModel.setReservationStatus(reservation, ReservationStatus.CANCELLED)
            listener.onStatusChanged()
            showCancelSuccessDialog()
            this.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showCancelSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cancellation Complete")
        builder.setMessage("Your booking has been successfully cancelled!")
        val dialog = builder.create()
        dialog.show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        val sizeDialog = Dialog(requireContext())
        sizeDialog.setContentView(R.layout.dialog_size_picker)
        sizeDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

    }

    private fun configureButtons() {
        .btnBack.setOnClickListener {
            this.dismiss()
        }
    }

    private fun configureBindings() {

        val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

        binding.start.text = reservation.startDate?.toDate()?.let { formatter.format(it) }
        binding.end.text = reservation.endDate?.toDate()?.let { formatter.format(it) }

        binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
        binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
        binding.titleText.text = listing.title
        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getGeneralLocation(location.latitude, location.longitude)
        }

//        binding.price.text = getString(R.string.listing_price_template, listing.price)
        binding.likes.text = listing.likes.toString()

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

        binding.mapView.getMapAsync { map ->
            listing.location?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

        val currentTimestamp = System.currentTimeMillis()

        if (reservation.status != ReservationStatus.APPROVED
            || reservation.rated
            || (reservation.startDate?.toDate()?.time ?: Long.MAX_VALUE) > currentTimestamp
        ) {
            binding.ratingBox.visibility = View.GONE
        } else {
            binding.ratingBox.visibility = View.VISIBLE
        }

//
//        if (reservation.status == ReservationStatus.PENDING) {
//            binding.cancelBtn.visibility = View.VISIBLE
//        } else {
//            binding.cancelBtn.visibility = View.GONE
//        }


        val reservationCreationTimePlus12Hours = (reservation.reservationDate?.toDate()?.time ?: Long.MAX_VALUE) + TimeUnit.HOURS.toMillis(12)
        val reservationStartTimeMinus48Hours = (reservation.startDate?.toDate()?.time ?: Long.MAX_VALUE) - TimeUnit.HOURS.toMillis(48)

        if (reservation.status != ReservationStatus.PENDING
            && (currentTimestamp > reservationCreationTimePlus12Hours || currentTimestamp > reservationStartTimeMinus48Hours)) {
            binding.cancelBtn.visibility = View.GONE
        } else {
            binding.cancelBtn.visibility = View.VISIBLE
        }

        if (reservation.status == ReservationStatus.APPROVED &&
            !reservation.paymentCompleted) {
            binding.payBtn.visibility = View.VISIBLE
        } else {
            binding.payBtn.visibility = View.GONE
        }
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.isScrollGesturesEnabled = false

        // Add a marker at a specific location
        listing.location?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            map?.addMarker(MarkerOptions().position(latLng))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            map.setOnMapClickListener {
                val mapDialogFragment = MapDialogFragment(null, latLng, true)
                mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
            }
        }
    }
}
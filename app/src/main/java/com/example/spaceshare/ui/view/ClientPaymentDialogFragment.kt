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


}
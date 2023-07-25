package com.example.spaceshare.ui.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.FragmentReservationBinding
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.User
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
//import com.example.spaceshare.utils.ImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.example.spaceshare.models.ReservationStatus.PENDING
import com.example.spaceshare.models.ReservationStatus.APPROVED
import com.example.spaceshare.models.ReservationStatus.CANCELLED
import com.example.spaceshare.models.ReservationStatus.COMPLETED
import com.example.spaceshare.models.ReservationStatus.DECLINED
import com.example.spaceshare.ui.viewmodel.ListingViewModel

@AndroidEntryPoint
class ReservationFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController

    private lateinit var binding: FragmentReservationBinding
    @Inject
    lateinit var viewModel: ReservationViewModel
    @Inject
    lateinit var listingViewModel: ListingViewModel

    private lateinit var allReservations : List<Reservation>

    private var showOnlyPending = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDatePeriod(start: Timestamp, end: Timestamp): String {
        val startDate = start.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endDate = end.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("MMM dd")
        val formattedStart = startDate.format(formatter)
        val formattedEnd = endDate.format(formatter)
        return "$formattedStart - $formattedEnd"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)
        configureBindings()
        viewModel.reservationLiveData.observe(viewLifecycleOwner) { reservations ->
            allReservations = reservations
            displayReservations()
        }

        viewModel.fetchReservations(User(auth.currentUser!!.uid, "first_name", "last_name"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureBindings() {
        binding.allReservationsFilter.setOnClickListener {
            if (showOnlyPending) {
                showOnlyPending = false
                displayReservations()
            }
        }
        binding.pendingReservationsFilter.setOnClickListener {
            if (!showOnlyPending) {
                showOnlyPending = true
                displayReservations()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayReservations() {
        val displayList = if (showOnlyPending) {
            allReservations.filter { it.status == PENDING }
        } else {
            allReservations
        }

            binding.reservationPage.removeAllViews()

            for (reservation in displayList) {

                val cardView = layoutInflater.inflate(R.layout.reservation_item, null) as CardView
                val viewPager: ViewPager2 =
                    cardView.findViewById(R.id.view_pager_reservation_images)
                val title: TextView = cardView.findViewById(R.id.reservation_title)
                val location: TextView = cardView.findViewById(R.id.reservation_location)
                val period: TextView = cardView.findViewById(R.id.reservation_period)
                val status: TextView = cardView.findViewById(R.id.reservation_status)

                title.text = reservation.listingTitle
                location.text = reservation.location

                if (reservation.startDate != null || reservation.endDate != null) {
                    period.text =
                        formatDatePeriod(
                            reservation.startDate ?: Timestamp(0, 0),
                            reservation.endDate ?: Timestamp(0, 0)
                        )
                } else {
                    period.text = "N/A"
                }
                status.text = when (reservation.status) {
                    PENDING -> "Pending"
                    APPROVED -> "Approved"
                    DECLINED -> "Declined"
                    CANCELLED -> "Cancelled"
                    COMPLETED -> "Completed"
                    else -> "ERROR"
                }

                if (reservation.previewPhoto != null) {
                    val photoAdapter : MutableList<String> = mutableListOf(reservation.previewPhoto)
                    viewPager.adapter =
                        ImageAdapter(photoAdapter.map { ImageModel(imagePath = it) })
                }

                // Add the CardView to the LinearLayout
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(8, 32, 8, 32)
                cardView.layoutParams = layoutParams
                cardView.radius = 25.0F
                binding.reservationPage.addView(cardView)
            }
    }

}
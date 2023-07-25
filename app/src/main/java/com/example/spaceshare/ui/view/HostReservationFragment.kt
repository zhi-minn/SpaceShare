//package com.example.spaceshare.ui.view
//
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import androidx.cardview.widget.CardView
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.navigation.NavController
//import androidx.navigation.findNavController
//import androidx.viewpager2.widget.ViewPager2
//import com.example.spaceshare.R
//import com.example.spaceshare.adapters.ImageAdapter
//import com.example.spaceshare.databinding.FragmentReservationBinding
//import com.example.spaceshare.models.ImageModel
//import com.example.spaceshare.models.Reservation
//import com.example.spaceshare.models.ReservationStatus
//import com.example.spaceshare.models.User
//import com.example.spaceshare.ui.viewmodel.ListingViewModel
//import com.example.spaceshare.ui.viewmodel.ReservationViewModel
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import dagger.hilt.android.AndroidEntryPoint
//import java.time.ZoneId
//import java.time.format.DateTimeFormatter
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class HostReservationFragment: Fragment() {
//    private var auth = FirebaseAuth.getInstance()
//    private lateinit var navController: NavController
//
//    private lateinit var binding: FragmentReservationBinding
//    @Inject
//    lateinit var viewModel: ReservationViewModel
//    @Inject
//    lateinit var listingViewModel: ListingViewModel
//
//    private lateinit var allReservations : List<Reservation>
//
//    private var showOnlyPending = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation, container, false)
//        return binding.root
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun formatDatePeriod(start: Timestamp, end: Timestamp): String {
//        val startDate = start.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//        val endDate = end.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//        val formatter = DateTimeFormatter.ofPattern("MMM dd")
//        val formattedStart = startDate.format(formatter)
//        val formattedEnd = endDate.format(formatter)
//        return "$formattedStart - $formattedEnd"
//    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)
//        configureBindings()
//
//        viewModel.reservationLiveData.observe(viewLifecycleOwner) { reservations ->
//            allReservations = reservations
//            displayReservations()
//        }
//        viewModel.fetchReservations(User(auth.currentUser!!.uid, "first_name", "last_name"))
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun configureBindings() {
//        binding.allReservationsFilter.setOnClickListener {
//            if (showOnlyPending) {
//                showOnlyPending = false
//                displayReservations()
//            }
//        }
//        binding.pendingReservationsFilter.setOnClickListener {
//            if (!showOnlyPending) {
//                showOnlyPending = true
//                displayReservations()
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun displayReservations() {
//        val displayList = if (showOnlyPending) {
//            allReservations.filter { it.status == ReservationStatus.PENDING }
//        } else {
//            allReservations
//        }
//
//        binding.reservationPage.removeAllViews()
//
//        for (reservation in displayList) {
//
//            val cardView = layoutInflater.inflate(R.layout.host_reservation_items, null) as CardView
//            val viewPager: ViewPager2 =
//                cardView.findViewById(R.id.profileImage)
//            val name: TextView = cardView.findViewById(R.id.host_reservation_name)
//            val title: TextView = cardView.findViewById(R.id.host_reservation_title)
//            val period: TextView = cardView.findViewById(R.id.host_reservation_period)
//            val spaceRequested: TextView = cardView.findViewById(R.id.host_reservation_space_required)
//            val status: TextView = cardView.findViewById(R.id.host_reservation_status)
//
//            name.text = "Request From: ${reservation.clientFirstName}"
//            title.text = reservation.listingTitle
//            spaceRequested.text = reservation.spaceRequested.toString()
//
//            if (reservation.startDate != null || reservation.endDate != null) {
//                period.text =
//                    formatDatePeriod(
//                        reservation.startDate ?: Timestamp(0, 0),
//                        reservation.endDate ?: Timestamp(0, 0)
//                    )
//            } else {
//                period.text = "N/A"
//            }
//            status.text = when (reservation.status) {
//                ReservationStatus.PENDING -> "Waiting for approval"
//                ReservationStatus.APPROVED -> "Approved"
//                ReservationStatus.DECLINED -> "Declined"
//                ReservationStatus.CANCELLED -> "Cancelled"
//                ReservationStatus.COMPLETED -> "Completed"
//                else -> "ERROR"
//            }
//
//            // user profile photo
//            val clientPhoto : String? = reservation.clientPhoto
//            if (clientPhoto != null) {
//                val photoAdapter : MutableList<String> = mutableListOf(clientPhoto)
//                viewPager.adapter =
//                    ImageAdapter(photoAdapter.map { ImageModel(imagePath = it) })
//            }
//
//            // Add the CardView to the LinearLayout
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            layoutParams.setMargins(8, 32, 8, 32)
//            cardView.layoutParams = layoutParams
//            cardView.radius = 25.0F
//            binding.reservationPage.addView(cardView)
//        }
//    }
//}
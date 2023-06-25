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
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentReservationBinding
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.orders.AllOrdersFragment
import com.example.spaceshare.ui.orders.PendingOrdersFragment
import com.example.spaceshare.ui.orders.SuccessfulOrdersFragment
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.utils.ImageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ReservationFragment : Fragment() {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController

    private lateinit var binding: FragmentReservationBinding
    @Inject
    lateinit var viewModel: ReservationViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservation, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDatePeriod(start: Timestamp, end: Timestamp): String {
        val startDate = start.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endDate = end.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val formattedStart = startDate.format(formatter)
        val formattedEnd = endDate.format(formatter)
        return "$formattedStart $formattedEnd"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
//        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

//        val fragmentList = arrayListOf<Fragment>(
//            AllOrdersFragment(),
//            SuccessfulOrdersFragment(),
//            PendingOrdersFragment()
//        )

//        val adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
//            override fun getItemCount() = fragmentList.size
//            override fun createFragment(position: Int) = fragmentList[position]
//        }

//        viewPager.adapter = adapter

//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when(position) {
//                0 -> "All Orders"
//                1 -> "Successful Orders"
//                else -> "Pending Orders"
//            }
//        }.attach()
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)
//        displayReservations()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayReservations() {
        viewModel.reservationLiveData.observe(viewLifecycleOwner) { reservations ->
            binding.reservationPage.removeAllViews()
            for (reservation in reservations) {
                val cardView = layoutInflater.inflate(R.layout.listing_item, null) as CardView
                val viewPager: ViewPager2 = cardView.findViewById(R.id.view_pager_listing_images)
                val location: TextView = cardView.findViewById(R.id.reservation_location)
                val period: TextView = cardView.findViewById(R.id.reservation_period)
                val status: TextView = cardView.findViewById(R.id.reservation_status)

                // get listing reference
                lateinit var geoLocation : String
                lateinit var previewPhoto : List<String>

                val documentId = reservation.listingId
                val documentRef = db.collection("listings").document(documentId ?: "")
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val data = documentSnapshot.data
                            geoLocation = (data?.get("location")?.toString() ?: null) as String
                            previewPhoto = (data?.get("photos") as? List<String>?)!!
                        } else {
                            throw Exception("Listing not found")
                        }
                    }
                    .addOnFailureListener { exception ->
                        throw exception
                    }


                location.text = geoLocation // TODO: get city by geo or add city to Listing model
                if (reservation.startDate != null || reservation.endDate != null) {
                    period.text =
                        formatDatePeriod(reservation.startDate ?: Timestamp(0, 0),
                        reservation.endDate ?: Timestamp(0, 0))
                } else {
                    period.text = "N/A"
                }
                status.text = when (reservation.status) {
                    0 -> "Pending"
                    1 -> "Approved"
                    2 -> "Declined"
                    3 -> "Completed"
                    4 -> "Cancelled"
                    else -> "ERROR"
                }

                if (previewPhoto != null) {
                    viewPager.adapter = ImageAdapter(previewPhoto!!)
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
        viewModel.fetchReservations(User("j577YevJRoZHgsKCRC9i1RLACZL2"))
    }

//    @RequiresApi(Build.VERSION_CODES.O) // TODO: Remove after Date (Calendar Date Picker) is finalized on the frontend
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        navController = findNavController()
//        auth = FirebaseAuth.getInstance()
//
//        val reserveButton = view.findViewById<Button>(R.id.reserveButton)
//        reserveButton.setOnClickListener {
//
//            val startDate = Date(2023, 6, 1)
//            val endDate = Date(2023, 6, 30)
//
//            val unit = view.findViewById<EditText>(R.id.unitTextInput).text.toString().toInt()
//
//            reserveListing(Timestamp(startDate), Timestamp(endDate), unit)
//        }
//    }

//    fun reserveListing(startDate: Timestamp, endDate: Timestamp, unit: Int) {
//        val clientId = auth.currentUser?.uid
//        val hostId = "host" // TODO: Replace dummy values
//        val listingId = "listing"
//
//        if (hostId == listingId) {
//            throw Exception("hostId cannot be the same as clientId")
//        }
//        println("before")
//        val reservation =
//            Reservation(hostId = hostId, clientId = clientId, listingId = listingId,
//                startDate = startDate, endDate = endDate, unit = unit,
//                isPending = true, isApproved = false)
//        db.collection("reservations")
//            .add(reservation)
//            .addOnFailureListener { e ->
//                // Failed to add reservation
//                throw Exception("Database failed to add reservation: ${e.message}", e)
//            }
//        println("after")
//    }
}
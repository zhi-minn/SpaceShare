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
import com.example.spaceshare.databinding.FragmentReservationBinding
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.User
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.utils.ImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ReservationFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
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

        // TODO: Reservation classifier switching tab
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
        displayReservations()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayReservations() {
        viewModel.reservationLiveData.observe(viewLifecycleOwner) { reservations ->
            binding.reservationPage.removeAllViews()
            for (reservation in reservations) {
                val cardView = layoutInflater.inflate(R.layout.reservation_item, null) as CardView
                val viewPager: ViewPager2 = cardView.findViewById(R.id.view_pager_reservation_images)
                val location: TextView = cardView.findViewById(R.id.reservation_location)
                val period: TextView = cardView.findViewById(R.id.reservation_period)
                val status: TextView = cardView.findViewById(R.id.reservation_status)

                // get listing reference
                val geoLocation = "Waterloo" // TODO: replace dummy values when listing detail is done
                val previewPhoto = listOf("JPEG_20230619_181925_5939538432909368723.jpg_179d1f4f-856a-47e9-ae15-4466ca4fb64b")

//                val documentId = reservation.listingId
//                val documentRef = db.collection("listings").document(documentId ?: "")
//                documentRef.get().await()
//                    .addOnSuccessListener { documentSnapshot ->
//                        if (documentSnapshot.exists()) {
//                            val data = documentSnapshot.data
//                            geoLocation = (data?.get("location")?.toString() ?: null) as String
//                            previewPhoto = (data?.get("photos") as? List<String>?)!!
//                        } else {
//                            throw Exception("Listing not found")
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        throw exception
//                    }




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
                    3 -> "Cancelled"
                    4 -> "Completed"
                    else -> "ERROR"
                }

                if (previewPhoto != null) {
                    viewPager.adapter = ImageAdapter(previewPhoto)
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

}
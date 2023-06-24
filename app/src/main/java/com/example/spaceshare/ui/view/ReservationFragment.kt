package com.example.spaceshare.ui.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentListingBinding
import com.example.spaceshare.databinding.FragmentReservationBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.utils.ImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
import com.google.firebase.Timestamp
import javax.inject.Inject

class ReserveFragment : Fragment() {

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

//    private fun displayReservations() {
//        viewModel.listingsLiveData.observe(viewLifecycleOwner) { listings ->
//            binding.listingPage.removeAllViews()
//            for (listing in listings) {
//                val cardView = layoutInflater.inflate(R.layout.listing_item, null) as CardView
//                val viewPager: ViewPager2 = cardView.findViewById(R.id.view_pager_listing_images)
//                val title: TextView = cardView.findViewById(R.id.listing_title)
//                val description: TextView = cardView.findViewById(R.id.listing_description)
//                val price: TextView = cardView.findViewById(R.id.listing_price)
//
//                // Set the listing data to the views
//                title.text = listing.title
//                description.text = listing.description
//                price.text = listing.price.toString()
//
//                // Load the listing image from Firebase Storage into the ImageView
//                if (listing.photos != null) {
//                    viewPager.adapter = ImageAdapter(listing.photos)
//                }
//
//                // Add the CardView to the LinearLayout
//                val layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                layoutParams.setMargins(8, 32, 8, 32)
//                cardView.layoutParams = layoutParams
//                cardView.radius = 25.0F
//                binding.listingPage.addView(cardView)
//            }
//        }
//        viewModel.fetchListings(User("j577YevJRoZHgsKCRC9i1RLACZL2"))
//    }

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
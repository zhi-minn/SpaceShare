package com.example.spaceshare.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.spaceshare.R
import com.example.spaceshare.SplashActivity
import com.example.spaceshare.databinding.FragmentCreateListingBinding
import com.example.spaceshare.databinding.FragmentDetailedPageBinding
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class DetailedPageFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var DetailedPage: DetailedPageFragment
    private lateinit var binding: FragmentDetailedPageBinding
    @Inject
    lateinit var reservationViewModel: ReservationViewModel

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_page, container, false)
//        return binding.root
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get house from intent extras
//        val intent = Intent(requireContext(), SplashActivity::class.java)

        // TODO: Populate views with listing data
//        binding.reserve_button.setOnClickListener {
//            reserveListing(binding.unitNumberInput.text.toString().toInt())
//        }
    }

    fun reserveListing(unit: Int) {
        val clientId = auth.currentUser?.uid
        val reservation = Reservation(hostId = "dummy", clientId = clientId, listingId="dummy",
            startDate = Timestamp.now(), endDate = Timestamp.now(), unit = unit,
            status = ReservationStatus.PENDING.toInt())
        // TODO: validation
        reservationViewModel.reserveListing(reservation)
//        binding.scrollView.visibility = View.GONE
//        binding.progressBar.visibility = View.VISIBLE
    }
}
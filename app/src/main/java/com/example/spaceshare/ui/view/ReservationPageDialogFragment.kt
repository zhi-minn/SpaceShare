package com.example.spaceshare.ui.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogReservationPageBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReservationPageDialogFragment(
    private val listing: Listing,
//    private val searchViewModel: SearchViewModel
): DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }
    @Inject
    lateinit var reservationViewModel: ReservationViewModel

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: DialogReservationPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogReservationPageBinding.inflate(inflater, container, false)

//        configureBindings()
//        configureButtons()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureBindings()
        configureButtons()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
    }

    private fun configureBindings() {
        binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
        binding.hostName.text = listing.hostId
        binding.houseName.text = listing.title
//        val startDate = searchViewModel.startTime

        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
//        val formattedDate = formatter.format(startDate)
        binding.pickedDate.text = "Jul 19"
        binding.lugguageSize.text = "0.5"// TODO
    }

    private fun configureButtons() {
        binding.dateEdit.setOnClickListener {
            // TODO: Implement date edit action
        }

        binding.sizeEdit.setOnClickListener {
            // TODO: Implement size edit action
        }

        binding.reserveBtn.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            val clientId = auth.currentUser?.uid
            val reservation = Reservation(
                hostId=listing.hostId, clientId=clientId, listingId=listing.id,
                startDate=Timestamp.now(), endDate=Timestamp.now(),
                unit=3.5, status=ReservationStatus.PENDING.toInt())
            reservationViewModel.reserveListing(reservation)
        }
    }

}



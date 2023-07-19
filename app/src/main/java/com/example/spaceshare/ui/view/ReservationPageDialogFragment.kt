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
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.utils.GeocoderUtil
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects


class ReservationPageDialogFragment(
    private val listing: Listing,
//    private val searchViewModel: SearchViewModel
): DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: DialogReservationPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogReservationPageBinding.inflate(inflater, container, false)

        configureBindings()
        configureButtons()

        return binding.root
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
            // TODO: implenmente reserve func
        }
    }

}



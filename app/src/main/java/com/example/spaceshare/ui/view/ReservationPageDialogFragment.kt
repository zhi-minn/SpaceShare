package com.example.spaceshare.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogReservationPageBinding
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.utils.GeocoderUtil
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ReservationPageDialogFragment(
    private val listing: Listing,
    private val searchViewModel: SearchViewModel
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

        // Get a reference to the Toolbar
        val toolbar: Toolbar = binding.root.findViewById(R.id.confirmPayToolbar)
        val toolbarTitle: TextView = binding.root.findViewById(R.id.toolbar_title)

        // Set the navigation icon and click listener
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_left)
        toolbar.setNavigationOnClickListener { dismiss() } // dismiss the DialogFragment when the back navigation icon is pressed

        // Set the title
        toolbarTitle.text = "Confirm"

        val finalRule = binding.root.findViewById<TextView>(R.id.finalRule)
        finalRule.text = "By selecting the button below, I agree to the ground rules and policy established by SpaceShare"
        configureBindings()
        configureButtons()

        return binding.root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
    }
    object GeocoderUtil {

        fun getCityName(context: Context, latitude: Double, longitude: Double): String? {
            val geocoder = Geocoder(context, Locale.getDefault())

            return try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }


    private fun configureBindings() {
        binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })

        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getCityName(requireContext(), location.latitude, location.longitude)
        }

        binding.houseName.text = listing.title

        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        if (searchViewModel.startTime.value == 0L) {
            val cal = Calendar.getInstance()

            // Get current date
            val curDate = formatter.format(cal.time)

            // Add 30 days to current date
            cal.add(Calendar.DATE, 30)
            val curDatePlus30Days = formatter.format(cal.time)

            binding.pickedDate.text = "$curDate - $curDatePlus30Days"
        } else {
            // Convert the startTime and endTime from Long to Date and format them
            val startDate = Date(searchViewModel.startTime.value!!)
            val endDate = Date(searchViewModel.endTime.value!!)

            val formattedStartDate = formatter.format(startDate)
            val formattedEndDate = formatter.format(endDate)

            binding.pickedDate.text = "$formattedStartDate - $formattedEndDate"
        }

        // Handle spaceRequired
        if (searchViewModel.spaceRequired.value == 0.0) {
            binding.lugguageSize.text = "1.0 cubic"
        } else {
            binding.lugguageSize.text = "${searchViewModel.spaceRequired.value} cubic"
        }
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



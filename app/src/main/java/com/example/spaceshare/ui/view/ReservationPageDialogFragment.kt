package com.example.spaceshare.ui.view


import android.app.Dialog
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogReservationPageBinding
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

import com.example.spaceshare.models.ReservationStatus.PENDING
import com.example.spaceshare.utils.MathUtil

@AndroidEntryPoint
class ReservationPageDialogFragment(
    private val listing: Listing,
    private val searchViewModel: SearchViewModel?
): DialogFragment() {
    private var startDate : Long? = 0
    private var endDate : Long? = 0
    private var unit: Double = 0.0

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

        // Get a reference to the Toolbar
        val toolbar: Toolbar = binding.root.findViewById(R.id.confirmPayToolbar)
        val toolbarTitle: TextView = binding.root.findViewById(R.id.toolbar_title)

        // Set the navigation icon and click listener
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_left)
        toolbar.setNavigationOnClickListener { dismiss() } // dismiss the DialogFragment when the back navigation icon is pressed

        // Set the title
        toolbarTitle.text = "Confirm"

        val finalRule = binding.root.findViewById<TextView>(R.id.finalRule)
        finalRule.text =
            "By selecting the button below, I agree to the ground rules and policy established by SpaceShare"
        configureBindings()
        configureButtons()

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        val sizeDialog = Dialog(requireContext())
        sizeDialog.setContentView(R.layout.dialog_size_picker)
        sizeDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

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
        binding.viewPagerListingImages.adapter =
            ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })

        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getCityName(requireContext(), location.latitude, location.longitude)
        }

        binding.houseName.text = listing.title

        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        if (searchViewModel == null) {
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
        // Chang: This should be stored in ReservationViewModel
        if (searchViewModel == null) {
            binding.lugguageSize.text = "1.0 cubic"
        } else {
            binding.lugguageSize.text = "${searchViewModel.spaceRequired.value} cubic"
        }

        binding.dates.setOnClickListener {
            binding.dateEdit.performClick()
        }

        binding.sizes.setOnClickListener {
            binding.sizeEdit.performClick()
        }
    }


    private fun configureButtons() {

        binding.dateEdit.setOnClickListener { openDatePicker() }
        binding.dates.setOnClickListener { openDatePicker() }

        binding.sizeEdit.setOnClickListener { openSizePicker() }
        binding.sizes.setOnClickListener { openSizePicker() }

        binding.reserveBtn.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val clientId = auth.currentUser?.uid

            // Calculate total cost for host analytics (since price is subject to possible change)
            val totalTime = Date(endDate!!).time - Date(startDate!!).time
            val totalDays = totalTime / (1000 * 60 * 60 * 24) + 1
            val totalCost = MathUtil.roundToTwoDecimalPlaces(listing.price * unit * totalDays)

            val reservation = Reservation(
                hostId=listing.hostId, clientId=clientId, listingId=listing.id, totalCost = totalCost,
                startDate=Timestamp(Date(startDate!!)), endDate=Timestamp(Date(endDate!!)),
                spaceRequested=unit, status= PENDING)
            reservationViewModel.reserveListing(reservation)

            // Show a confirmation dialog
            showDialogThenDismiss()
        }
    }

    private fun showDialogThenDismiss() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reservation Confirmation")
        builder.setMessage("Your reservation is complete!")
        builder.setPositiveButton("OK") { dialog, which ->
            // Dismiss this DialogFragment when the user confirms
            this.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            binding.pickedDate.text = dateRangePicker.headerText
            startDate = dateRangePicker.selection?.first
            endDate = dateRangePicker.selection?.second
        }
            // SearchViewModel should not be used here, this should be stored in ReservationViewModel
//            dateRangePicker.selection?.first?.let { it1 -> searchViewModel.setStartTime(it1) }
//            dateRangePicker.selection?.second?.let { it1 -> searchViewModel.setEndTime(it1) }

        dateRangePicker.show(parentFragmentManager, TAG)
    }

    private fun openSizePicker() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_size_picker)

        val sizeValue: TextView = dialog.findViewById(R.id.tv_size_value)
        val increaseButton: ImageButton = dialog.findViewById(R.id.btn_increase)
        val decreaseButton: ImageButton = dialog.findViewById(R.id.btn_decrease)

        // Disable the decrease button initially if the size is already 0.5
        decreaseButton.isEnabled = sizeValue.text.toString().toDouble() > 0.5

        // Disable the increase button initially if the size is already 100
        increaseButton.isEnabled = sizeValue.text.toString().toDouble() < 100

        increaseButton.setOnClickListener {
            var value = sizeValue.text.toString().toDouble()
            if (value < 100) {
                value += 0.5
                sizeValue.text = value.toString()

                // Enable decrease button when size is more than 0.5
                decreaseButton.isEnabled = true

                // Disable increase button if the size reaches 100
                if (value >= 100) {
                    increaseButton.isEnabled = false
                }
            }
        }

        decreaseButton.setOnClickListener {
            var value = sizeValue.text.toString().toDouble()
            if (value > 0.5) {
                value -= 0.5
                sizeValue.text = value.toString()

                // Enable increase button when size is less than 100
                increaseButton.isEnabled = true

                // Disable decrease button if the size reaches 0.5
                if (value <= 0.5) {
                    decreaseButton.isEnabled = false
                }
            }
        }

        dialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
            val selectedValue = sizeValue.text.toString().toDouble()
            unit = selectedValue
            binding.lugguageSize.text = "$selectedValue cubic"
            dialog.dismiss()
        }
        dialog.show()
    }
}



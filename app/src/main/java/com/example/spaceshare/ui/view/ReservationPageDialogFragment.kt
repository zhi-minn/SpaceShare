package com.example.spaceshare.ui.view


import android.app.Dialog
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogReservationPageBinding
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Message
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.toInt
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.spaceshare.enums.DeclareItemType

import com.example.spaceshare.models.ReservationStatus.PENDING
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.ChatViewModel
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.example.spaceshare.utils.MathUtil
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationPageDialogFragment(
    private val listing: Listing,
    private val searchViewModel: SearchViewModel?
): DialogFragment() {
    private var startDate : Long? = null
    private var endDate : Long? = null
    private var unit: Double? = 1.0

    private var itemTypes: MutableMap<DeclareItemType, String> = mutableMapOf()

    private var unitAvailable: Double = 10.0

    private var auth = FirebaseAuth.getInstance()

    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    lateinit var reservationViewModel: ReservationViewModel

    @Inject
    lateinit var listingViewModel: ListingViewModel

    @Inject
    lateinit var messagesViewModel: MessagesViewModel

    @Inject
    lateinit var chatViewModel: ChatViewModel

    private lateinit var binding: DialogReservationPageBinding

    private lateinit var client : User

//    private val realTimeDB = Firebase.database
//    private val baseMessagesRef = realTimeDB.reference.child("messages")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogReservationPageBinding.inflate(inflater, container, false)

//        reservationViewModel.userInfoLiveData.observe(viewLifecycleOwner) { user ->
//            client = user
//        }
//        reservationViewModel.fetchUserInfo(auth.currentUser!!.uid)

        CoroutineScope(Dispatchers.IO).launch {
            client = reservationViewModel.fetchUserInfo(auth.currentUser!!.uid)!!
        }

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

        if(searchViewModel != null) {
            searchViewModel.let { model ->
                model.startTime.value?.let { startTimeInMillis ->
                    startDate = startTimeInMillis
                }

                model.endTime.value?.let { endTimeInMillis ->
                    endDate = endTimeInMillis
                }

                model.spaceRequired.value?.let { spaceRequiredValue ->
                    unit = spaceRequiredValue
                }
            }
        } else {
            // if searchViewModel is null, use current date for startDate
            startDate = System.currentTimeMillis()

            // set endDate to 30 days from now
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 30)
            endDate = calendar.timeInMillis

            // set default spaceRequired to 1.0
            unit = 1.0
        }

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


//    private fun configureBindings() {
//        binding.viewPagerListingImages.adapter =
//            ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
//
//        binding.location.text = listing.location?.let { location ->
//            GeocoderUtil.getCityName(requireContext(), location.latitude, location.longitude)
//        }
//
//        binding.houseName.text = listing.title
//
//        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
//        if (searchViewModel?.startTime?.value?.toInt() == 0) {
//            val cal = Calendar.getInstance()
//
//            // Get current date
//            val curDate = formatter.format(cal.time)
//
//            // Add 30 days to current date
//            cal.add(Calendar.DATE, 30)
//            val curDatePlus30Days = formatter.format(cal.time)
//
//            binding.pickedDate.text = "$curDate - $curDatePlus30Days"
//        } else {
//            // Convert the startTime and endTime from Long to Date and format them
//            val startDate = Date(searchViewModel?.startTime?.value!!)
//            val endDate = Date(searchViewModel.endTime.value!!)
//
//            val formattedStartDate = formatter.format(startDate)
//            val formattedEndDate = formatter.format(endDate)
//
//            binding.pickedDate.text = "$formattedStartDate - $formattedEndDate"
//        }
//
//        // Handle spaceRequired
//        // Chang: This should be stored in ReservationViewModel
//        if (searchViewModel?.spaceRequired?.value?.toInt() == 0) {
//            binding.lugguageSize.text = "1.0 cubic"
//        } else {
//            binding.lugguageSize.text = "${searchViewModel.spaceRequired.value} cubic"
//        }
//
//        binding.dates.setOnClickListener {
//            binding.dateEdit.performClick()
//        }
//
//        binding.sizes.setOnClickListener {
//            binding.sizeEdit.performClick()
//        }
//    }
    private fun setEmptyCheck() {
        binding.reserveBtn.isEnabled = itemTypes.isNotEmpty()
    }

    fun updateItems(input: MutableMap<DeclareItemType, String>) {
        itemTypes.clear()
        itemTypes = input
        setEmptyCheck()
    }

    fun getItems(): MutableMap<DeclareItemType, String> {
        return itemTypes.toMutableMap()
    }

    private fun configureBindings() {
        binding.viewPagerListingImages.adapter =
            ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })

        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getCityName(requireContext(), location.latitude, location.longitude)
        }

        binding.houseName.text = listing.title

        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())

        // Get date range from ReservationViewModel
//        val startDate = reservationViewModel.startDate.value
//        val endDate = reservationViewModel.endDate.value

        // Handle dates
        if (startDate == 0L) {
            val cal = Calendar.getInstance()
            startDate = cal.timeInMillis

            // Get current date
            val curDate = formatter.format(cal.time)

            // Add 30 days to current date
            cal.add(Calendar.DATE, 30)

            endDate = cal.timeInMillis

            val curDatePlus30Days = formatter.format(cal.time)

            binding.pickedDate.text = "$curDate - $curDatePlus30Days"
        } else {
            // Convert the startDate and endDate from Long to Date and format them
            val formattedStartDate = formatter.format(startDate?.let { Date(it) })
            val formattedEndDate = formatter.format(endDate?.let { Date(it) })

            binding.pickedDate.text = "$formattedStartDate - $formattedEndDate"
        }

        CoroutineScope(Dispatchers.IO).launch {
            unitAvailable = reservationViewModel.getAvailableSpace(listing, startDate!!, endDate!!)
            binding.availableSpace.text = unitAvailable.toString()
        }

        // Handle spaceRequired
        // Get spaceRequired from ReservationViewModel
//        val spaceRequired = reservationViewModel.spaceRequired.value

        if (unit == 0.0) {
            unit = 1.0
            binding.lugguageSize.text = "1.0 cubic"
        } else {
            binding.lugguageSize.text = "$unit cubic"
        }


        binding.dates.setOnClickListener {
            binding.dateEdit.performClick()
        }

        binding.sizes.setOnClickListener {
            binding.sizeEdit.performClick()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureButtons() {

        binding.dateEdit.setOnClickListener { openDatePicker() }
        binding.dates.setOnClickListener { openDatePicker() }

        binding.sizeEdit.setOnClickListener { openSizePicker() }
        binding.sizes.setOnClickListener { openSizePicker() }

        binding.declareButton.setOnClickListener {
            val itemDeclarationFragment = ItemDeclarationFragment(this)
            itemDeclarationFragment.show(
                Objects.requireNonNull(childFragmentManager),
                "ItemDeclarationFragment"
            )
        }

        binding.reserveBtn.apply {
            setEmptyCheck()
            setOnClickListener {
                auth = FirebaseAuth.getInstance()
                val clientId = auth.currentUser?.uid

                // Calculate total cost for host analytics (since price is subject to possible change)
                val totalTime = Date(endDate!!).time - Date(startDate!!).time
                val totalDays = totalTime / (1000 * 60 * 60 * 24) + 1
                val totalCost = MathUtil.roundToTwoDecimalPlaces(listing.price * unit!! * totalDays)

                val location = listing.location?.let { location ->
                    com.example.spaceshare.utils.GeocoderUtil.getGeneralLocation(
                        location.latitude,
                        location.longitude
                    )
                }

                val previewPhoto = listing.photos[0]

                if (startDate!!.toInt() == 0) {
                    val cal = Calendar.getInstance()
                    startDate = cal.timeInMillis
                    cal.add(Calendar.DATE, 30)
                    endDate = cal.timeInMillis
                }

                var msgText = binding.messageToHost.text.toString()

                // If user didn't input any message, set a default greeting message
                if (msgText.isBlank()) {
                    msgText = "Hi, I'm interested in renting your space."
                }


                CoroutineScope(Dispatchers.IO).launch {
                    val spaceReservedSuccess =
                        reservationViewModel.reserveSpace(unit!!, listing, startDate!!, endDate!!)
                    if (!spaceReservedSuccess) {
                        showFailureDialog("Failed to reserve space, please adjust unit or reselect dates and try again")
                    }
                }

                val reservation = unit?.let { it1 ->
                    Reservation(
                        hostId = listing.hostId,
                        clientId = clientId,
                        listingId = listing.id,
                        totalCost = totalCost,
                        startDate = Timestamp(Date(startDate!!)),
                        endDate = Timestamp(Date(endDate!!)),
                        spaceRequested = it1,
                        status = PENDING,
                        listingTitle = listing.title,
                        location = location!!,
                        previewPhoto = previewPhoto,
                        clientFirstName = client.firstName,
                        clientLastName = client.lastName,
                        clientPhoto = client.photoPath,
                        message = msgText,
                        items = itemTypes.mapKeys { (key, _) -> key.toString() }.toMap()
                    )
                }

                if (reservation != null) {
                    reservationViewModel.reserveListing(reservation)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val chat = messagesViewModel.createChatWithHost(listing)
                    chatViewModel.setChat(chat)
                    chatViewModel.sendMessage(msgText)
                }
                // Show a confirmation dialog
                showDialogThenDismiss()
            }
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

    private fun showFailureDialog(message : String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reservation Failed")
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()
    }

    private fun openDatePicker() {
//        val constraintsBuilder = CalendarConstraints.Builder()
//
//        val dateRangePicker =
//            MaterialDatePicker.Builder.dateRangePicker()
//                .setTitleText("Select Dates")
//                .setCalendarConstraints(constraintsBuilder.build())
//                .build()
        val now = MaterialDatePicker.todayInUtcMilliseconds()

        // Validation that the date is after or equal to now
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(now))

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            binding.pickedDate.text = dateRangePicker.headerText
            startDate = dateRangePicker.selection?.first
            endDate = dateRangePicker.selection?.second
//            searchViewModel.startTime.value = dateRangePicker.selection?.first
//            searchViewModel.endTime.value = dateRangePicker.selection?.second
            searchViewModel?.setStartTime(dateRangePicker.selection?.first ?: 0)
            searchViewModel?.setEndTime(dateRangePicker.selection?.second ?: 0)

            CoroutineScope(Dispatchers.IO).launch {
                unitAvailable = reservationViewModel.getAvailableSpace(listing, startDate!!, endDate!!)
                binding.availableSpace.text = unitAvailable.toString()
            }
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

        sizeValue.text = unit.toString()
        // Disable the decrease button initially if the size is already 0.5
        decreaseButton.isEnabled = sizeValue.text.toString().toDouble() > 0.5

        // Disable the increase button initially if the size is already 100
        increaseButton.isEnabled = sizeValue.text.toString().toDouble() < unitAvailable

        increaseButton.setOnClickListener {
            var value = sizeValue.text.toString().toDouble()
            if (value <= unitAvailable) {
                value += 0.5
                sizeValue.text = value.toString()

                // Enable decrease button when size is more than 0.5
                decreaseButton.isEnabled = true

                // Disable increase button if the size reaches 100
                if (value > unitAvailable) {
                    showFailureDialog("Not enough space available for the selected dates. \n Please edit your space or adjust dates.")
                    value -= 0.5
                    sizeValue.text = value.toString()
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
            searchViewModel?.setSpaceRequired(selectedValue)
            dialog.dismiss()
        }
        dialog.show()
    }
}



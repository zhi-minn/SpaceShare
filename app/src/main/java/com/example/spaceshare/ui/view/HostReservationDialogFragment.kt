package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogHostReservationBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HostReservationDialogFragment(
    private val reservation: Reservation,
    private val listing: Listing,
    private val listener: OnReservationStatusChangedListener
) : DialogFragment(){

    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    lateinit var reservationViewModel: ReservationViewModel

    @Inject
    lateinit var messagesViewModel: MessagesViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private lateinit var binding: DialogHostReservationBinding


//    lateinit var listing:Listing

    interface OnReservationStatusChangedListener {
        fun onStatusChanged()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogHostReservationBinding.inflate(inflater, container, false)

        // Get a reference to the Toolbar
        val toolbar: Toolbar = binding.root.findViewById(R.id.ReservationToolbar)
        val toolbarTitle: TextView = binding.root.findViewById(R.id.toolbar_title)

        // Set the navigation icon and click listener
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_left)
        toolbar.setNavigationOnClickListener { dismiss() } // dismiss the DialogFragment when the back navigation icon is pressed

        // Set the title
        toolbarTitle.text = "Reservation"

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

//        val db = FirebaseFirestore.getInstance()
//
//
//        reservation.listingId?.let {
//            db.collection("listings")
//                .document(it)
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    listing = documentSnapshot.toObject(Listing::class.java)!!
//                    // You can now use `user`
//                    if (listing != null) {
//                        Log.d("listing id", listing.id)
//                    }
//
//                }
//                .addOnFailureListener { e -> Log.w("Error getting document", e) }
//        }
    }

    private fun configureBindings() {

        val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

        binding.start.text = reservation.startDate?.toDate()?.let { formatter.format(it) }
        binding.end.text = reservation.endDate?.toDate()?.let { formatter.format(it) }
        binding.textView5.text = reservation.spaceRequested.toString()
        //todo: binding.messageTextView.text = reservation.message
        binding.messageTextView.text = "this is a test msg" // delete after todo
        binding.name.text = "${reservation.clientFirstName} ${reservation.clientLastName}"
        binding.phoneNumber.text = "123-1234-1234" // todo: grab
        binding.email.text = "123@gmail.com" //todo: grab
        //todo:binding.verified.text = if (reservation.verified == true) "Yes" else "No"

        if (reservation.status == ReservationStatus.PENDING) {
            binding.decisionBox.visibility = View.VISIBLE
        } else {
            binding.decisionBox.visibility = View.GONE
        }
    }


    private fun configureButtons(){



//        binding.messageBtn.isGone = hideMessageHostButton

        binding.messageBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val chat = reservation.clientId?.let { it1 ->
                    messagesViewModel.createChatWithID(
                        listing,
                        it1
                    )
                }
                chat?.let { it1 -> ChatDialogFragment(it1, shouldRefreshChatsList = false) }?.show(
                    childFragmentManager,
                    "chatDialog"
                )
            }
        }

        binding.acceptBtn.setOnClickListener {
            val status = ReservationStatus.APPROVED
            reservationViewModel.setReservationStatus(reservation = reservation, status = status)
            listener.onStatusChanged()
            showDialogThenDismiss(true)
        }

        binding.rejectBtn.setOnClickListener {
            val status = ReservationStatus.DECLINED
            reservationViewModel.setReservationStatus(reservation = reservation, status = status)
            listener.onStatusChanged()
            showDialogThenDismiss(false)
        }
    }

    private fun showDialogThenDismiss(isAccepted: Boolean) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Request Confirmation")
        if (isAccepted){
            builder.setMessage("You have accepted the request!")
        }
        else{
            builder.setMessage("You have declined the request!")
        }

        builder.setPositiveButton("OK") { dialog, which ->
            // Dismiss this DialogFragment when the user confirms
            this.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}

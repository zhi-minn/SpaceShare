package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogHostReservationBinding
import com.example.spaceshare.enums.DeclareItemType
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.ChatViewModel
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @Inject
    lateinit var chatViewModel: ChatViewModel

    private lateinit var binding: DialogHostReservationBinding

    private lateinit var client : User

//    lateinit var listing:Listing

    interface OnReservationStatusChangedListener {
        fun onStatusChanged()
    }
    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun findItemType(type: DeclareItemType): Boolean {
        return reservation.items!!.contains(type.toString())
    }

    private fun configureBindings() {
        val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

        CoroutineScope(Dispatchers.IO).launch {
            client = reservation.clientId?.let { reservationViewModel.fetchUserInfo(it) }!!

            withContext(Dispatchers.Main) {
                binding.start.text = reservation.startDate?.toDate()?.let { formatter.format(it) }
                binding.end.text = reservation.endDate?.toDate()?.let { formatter.format(it) }
                binding.textView5.text = reservation.spaceRequested.toString()
                binding.clothingItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.ClOTHING)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.ClOTHING.toString()) == "") {
                        "Clothing"
                    } else {
                        "Clothing"
                    }
                }

                binding.documentsItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.BOOKS_AND_DOCUMENTS)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.BOOKS_AND_DOCUMENTS.toString()) == "") {
                        "Books and Documents"
                    } else {
                        "Books and Documents"
                    }
                }

                binding.furnitureItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.FURNITURE)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.FURNITURE.toString()) == "") {
                        "Small Furniture"
                    } else {
                        "Small Furniture"
                    }
                }

                binding.sportAndRecreationalItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.SPORT_AND_RECREATIONAL)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.SPORT_AND_RECREATIONAL.toString()) == "") {
                        "Sport and Recreational Equipment"
                    } else {
                        "Sport and Recreational Equipment"
                    }
                }

                binding.applianceItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.APPLIANCE)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.APPLIANCE.toString()) == "") {
                        "Small Appliances"
                    } else {
                        "Small Appliances"
                    }
                }

                binding.necessaryItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.DAILY_NECESSARY)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.DAILY_NECESSARY.toString()) == "") {
                        "Daily Necessaries"
                    } else {
                        "Daily Necessaries"
                    }
                }

                binding.mementosItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.MEMENTOS)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = if (reservation.items?.get(DeclareItemType.MEMENTOS.toString()) == "") {
                        "Mementos and Collectibles"
                    } else {
                        "Mementos and Collectibles"
                    }
                }

                binding.otherItemsList.apply {
                    visibility = if (findItemType(DeclareItemType.OTHERS)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    text = "Others"
                }

                binding.messageTextView.text = reservation.message
                binding.name.text = "${client.firstName} ${client.lastName}"
                binding.phoneNumber.text = if (client.phoneNumber == "")
                    "No phone number provided" else client.phoneNumber
                binding.email.text = if (client.email == null) "No email provided" else client.email

                binding.verified.text = if (client.isVerified == 1) "Yes" else "No"

                if (reservation.status == ReservationStatus.PENDING) {
                    binding.decisionBox.visibility = View.VISIBLE
                } else {
                    binding.decisionBox.visibility = View.GONE
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
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

            val acceptMsgText = "I've accepted your request, looking forward to host you!"

            CoroutineScope(Dispatchers.IO).launch {
                val chat = reservation.clientId?.let { it1 ->
                    messagesViewModel.createChatWithID(
                        listing,
                        it1
                    )
                }
                if (chat != null) {
                    chatViewModel.setChat(chat)
                }
                chatViewModel.sendMessage(acceptMsgText)
            }

            showDialogThenDismiss(true)
        }

        binding.rejectBtn.setOnClickListener {
            val status = ReservationStatus.DECLINED
            reservationViewModel.setReservationStatus(reservation = reservation, status = status)
            listener.onStatusChanged()

            val rejectMsgText = "Sorry, I can't accept your request right now. Hope to see you again."

            CoroutineScope(Dispatchers.IO).launch {
                val chat = reservation.clientId?.let { it1 ->
                    messagesViewModel.createChatWithID(
                        listing,
                        it1
                    )
                }
                if (chat != null) {
                    chatViewModel.setChat(chat)
                }
                chatViewModel.sendMessage(rejectMsgText)
            }

            showDialogThenDismiss(false)
        }
    }

    private fun showDialogThenDismiss(isAccepted: Boolean) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Request Confirmation")
        if (isAccepted){
            builder.setMessage("You have accepted the request and sent a welcome message to the client!")
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

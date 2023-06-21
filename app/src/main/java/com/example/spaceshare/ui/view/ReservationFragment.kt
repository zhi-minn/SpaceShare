package com.example.spaceshare.ui.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class ReserveFragment : Fragment() {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.listing_item, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O) // TODO: Remove after Date (Calendar Date Picker) is finalized on the frontend
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        val reserveButton = view.findViewById<Button>(R.id.reserveButton)
        reserveButton.setOnClickListener {

            val startDate = LocalDate.of(2023, 6, 1)
            val endDate = LocalDate.of(2023, 6, 30)

            val unit = view.findViewById<EditText>(R.id.unitTextInput).text.toString().toInt()

            reserveListing(startDate, endDate, unit)
        }
    }

    private fun reserveListing(startDate: LocalDate, endDate: LocalDate, unit: Int) {
        val clientId = auth.currentUser?.uid
        val hostId = "host" // TODO: Replace dummy values
        val listingId = "listing"

        if (hostId == listingId) {
            throw Exception("hostId cannot be the same as clientId")
        }

        val reservation =
            Reservation(hostId = hostId, clientId = clientId, listingId = listingId,
                startDate = startDate, endDate = endDate, unit = unit,
                isPending = true, isApproved = false)
        db.collection("reservations")
            .add(reservation)
            .addOnFailureListener { e ->
                // Failed to add reservation
                throw Exception("Database failed to add reservation: ${e.message}", e)
            }
    }
}
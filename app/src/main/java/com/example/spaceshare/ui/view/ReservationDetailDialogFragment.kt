package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.ReservationItemDetailBinding
import com.example.spaceshare.models.Reservation

class ReservationDetailDialogFragment(
    private val reservation: Reservation
): DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: ReservationItemDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.reservation_item_detail, container, false)
        configureBindings()
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }
        return binding.root
    }

    private fun configureBindings() {
        binding.location.text = when (reservation.status) {
            0 -> "Pending"
            1 -> "Approved"
            2 -> "Declined"
            3 -> "Cancelled"
            4 -> "Completed"
            else -> "ERROR"
        }

        binding.price.text = reservation.startDate?.toDate().toString()

        binding.title.text = reservation.hostId.toString()

        binding.location.text = "Waterloo"
    }

}
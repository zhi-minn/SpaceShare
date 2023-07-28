package com.example.spaceshare.ui.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.databinding.DialogClientPaymentBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.util.Date
import java.util.Objects
import javax.inject.Inject


@AndroidEntryPoint
class ClientPaymentDialogFragment(
    private val reservation: Reservation,
    private val listing: Listing
) : DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    lateinit var listingViewModel: ListingViewModel

    @Inject
    lateinit var reservationViewModel: ReservationViewModel

    private lateinit var binding: DialogClientPaymentBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogClientPaymentBinding.inflate(inflater, container, false)

        configureBindings()
//        configureButtons()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmPayBtn.setOnClickListener {
            val clientPaymentDialogFragment = ClientPaymentDialogFragment(reservation, listing)
            clientPaymentDialogFragment.show(
                Objects.requireNonNull(childFragmentManager),
                "ClientPaymentDialogFragment"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureBindings() {
        val totalTime = reservation.endDate!!.toDate().time - reservation.startDate!!.toDate().time
        val totalDays = totalTime / (1000 * 60 * 60 * 24) + 1

        binding.priceCalculationText.text = formatPriceText(listing.price.toString(), totalDays.toString())
        binding.priceCalculationAmount.text = formatWithCurrency(reservation.totalCost.toInt().toString())

        val ONTARIO_TAX = 13 // TODO: generate tax based on region
        binding.taxText.text = formatTaxText(ONTARIO_TAX.toString())
        val tax = (reservation.totalCost * ONTARIO_TAX / 100).toInt()
        binding.taxAmount.text = formatWithCurrency(tax.toString())

        binding.totalPrice.text = formatWithCurrency((reservation.totalCost.toInt() + tax).toString())
    }

    private fun formatWithCurrency(price : String): String {
        return "$price  CAD"
    }

    private fun formatPriceText(pricePerDay : String, days : String): String {
        return formatWithCurrency(pricePerDay) + "  x  $days  nights"
    }

    private fun formatTaxText(tax: String): String {
        return "Taxes $tax%"
    }


}
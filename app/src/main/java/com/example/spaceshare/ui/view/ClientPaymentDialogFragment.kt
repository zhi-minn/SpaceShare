package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import android.graphics.Typeface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogClientPaymentBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
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

    private lateinit var host : User


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogClientPaymentBinding.inflate(inflater, container, false)

        val toolbar: Toolbar = binding.root.findViewById(R.id.confirmPayToolbar2)
        val toolbarTitle: TextView = binding.root.findViewById(R.id.toolbar_title)

        // Set the navigation icon and click listener
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_left)
        toolbar.setNavigationOnClickListener { dismiss() } // dismiss the DialogFragment when the back navigation icon is pressed


        configureBindings()
//        configureButtons()

        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        val sizeDialog = Dialog(requireContext())
        sizeDialog.setContentView(R.layout.dialog_size_picker)
        sizeDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmPayBtn.setOnClickListener {
//            val clientPaymentDialogFragment = ClientPaymentDialogFragment(reservation, listing)
//            clientPaymentDialogFragment.show(
//                Objects.requireNonNull(childFragmentManager),
//                "ClientPaymentDialogFragment"
//            )
            reservationViewModel.setReservationPaid(reservation, true)
            showPaymentCompletedDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureBindings() {
        val totalTime = reservation.endDate!!.toDate().time - reservation.startDate!!.toDate().time
        val totalDays = totalTime / (1000 * 60 * 60 * 24) + 1

        val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        binding.start.text = reservation.startDate?.toDate()?.let { formatter.format(it) }
        binding.end.text = reservation.endDate?.toDate()?.let { formatter.format(it) }
        binding.priceCalculationText.text = formatPriceText(listing.price.toString(), totalDays.toString())
        binding.priceCalculationAmount.text = formatWithCurrency(reservation.totalCost.toInt().toString())

        val ONTARIO_TAX = 13 // TODO: generate tax based on region
        binding.taxText.text = formatTaxText(ONTARIO_TAX.toString())
        val tax = (reservation.totalCost * ONTARIO_TAX / 100).toInt()
        binding.taxAmount.text = formatWithCurrency(tax.toString())

        binding.totalPrice.text = formatWithCurrency((reservation.totalCost.toInt() + tax).toString())

        CoroutineScope(Dispatchers.IO).launch {
            host = reservation.hostId?.let { reservationViewModel.fetchUserInfo(it) }!!

            val boldSpan = StyleSpan(Typeface.BOLD)
            val underlineSpan = UnderlineSpan()
            val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))
            val lightBlackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_black))

            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append("Host E-Transfer address: ", boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.setSpan(blackColor, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            host.email?.let { email ->
                spannableStringBuilder.append(email, underlineSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(lightBlackColor, spannableStringBuilder.length - email.length, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            withContext(Dispatchers.Main) {
                binding.hostEmail.text = spannableStringBuilder
            }
        }


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

    private fun showPaymentCompletedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thank you!")
        builder.setMessage("The host will soon reach out to you about check-in details!")
        builder.setPositiveButton("OK") { dialog, which ->
            this.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}
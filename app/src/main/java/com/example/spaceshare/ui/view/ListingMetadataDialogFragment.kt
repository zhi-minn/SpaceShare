package com.example.spaceshare.ui.view

import MapDialogFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.CropActivity
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.databinding.DialogCreateListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.ListingMetadataViewModel
import com.example.spaceshare.utils.DecimalInputFilter
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class ListingMetadataDialogFragment(
    private val listing: Listing? = null
) : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: DialogCreateListingBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject
    lateinit var listingMetadataViewModel: ListingMetadataViewModel
    private var listener: ListingMetadataViewModel.ListingMetadataDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        if (listing != null) listingMetadataViewModel.setListing(listing)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = parentFragment
        if (parentFragment is ListingMetadataViewModel.ListingMetadataDialogListener) {
            listener = parentFragment
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_listing, container, false)
        binding.progressBar.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        configureListeners()
        configureButtons()
        configureFilters()
        configureCropActivity()
        configureObservers()
    }

    private fun configureListeners() {
        binding.priceInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.priceInput.text.toString().isEmpty()) {
                val defaultValue = 0.00
                binding.priceInput.setText(defaultValue.toString())
            }
        }
    }

    private fun configureButtons() {
        // Close button
        binding.btnCloseListing.setOnClickListener {
            this.dismiss()
        }

        // Add photo button
        binding.btnAddPhoto.setOnClickListener {
            startForResult.launch(Intent(requireActivity(), CropActivity::class.java))
        }

        // Space increment/decrement buttons
        binding.btnMinusSpace.setOnClickListener {
            listingMetadataViewModel.decrementSpaceAvailable()
        }
        binding.btnAddSpace.setOnClickListener {
            listingMetadataViewModel.incrementSpaceAvailable()
        }

        // Maps
        binding.btnOpenMaps.setOnClickListener {
            var latLng: LatLng? = null
            listingMetadataViewModel.listingLiveData.value?.location?.let {
                latLng = LatLng(it.latitude, it.longitude)
            }
            val mapDialogFragment = MapDialogFragment(listingMetadataViewModel, latLng)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
        }

        // Publish button
        binding.btnPublish.setOnClickListener {
            listingMetadataViewModel.setListing(binding.titleTextInput.text.toString(),
                binding.priceInput.text.toString().toDouble(),
                binding.descriptionTextInput.text.toString(),
                getCheckedAmenities())
        }
    }

    private fun configureFilters() {
        binding.priceInput.filters = arrayOf<InputFilter>(DecimalInputFilter)
    }

    private fun configureObservers() {
        // Listing metadata
        listingMetadataViewModel.listingLiveData.observe(viewLifecycleOwner) { listing ->
            binding.titleTextInput.setText(listing.title)
            binding.priceInput.setText(listing.price.toString())
            binding.descriptionTextInput.setText(listing.description)
            binding.spaceText.text = listing.spaceAvailable.toString()
            val amenities = Amenity.values().filter { listing.amenities.contains(it) }
            for (amenity in amenities) {
                when (amenity) {
                    Amenity.SURVEILLANCE -> binding.surveillance.isChecked = true
                    Amenity.CLIMATE_CONTROLLED -> binding.climateControlled.isChecked = true
                    Amenity.WELL_LIT-> binding.lighting.isChecked = true
                    Amenity.ACCESSIBILITY -> binding.accessibility.isChecked = true
                    Amenity.WEEKLY_CLEANING -> binding.cleanliness.isChecked = true
                }
            }

            listing.location?.let {
                binding.parsedLocation.text = GeocoderUtil.getAddress(it.latitude, it.longitude)
            }
        }

        // Images
        listingMetadataViewModel.images.observe(viewLifecycleOwner) { images ->
            binding.viewPagerListingImages.adapter = ImageAdapter(images)
            binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
        }

        // Spaces
        listingMetadataViewModel.spaceAvailable.observe(viewLifecycleOwner) { spaces ->
            binding.spaceText.text = spaces.toString()
        }

        // Validation
        listingMetadataViewModel.validateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                binding.scrollView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                val snackbar = Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.error_red, null))
                val tv: TextView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text)
                tv.setTextColor(resources.getColor(R.color.black, null))
                snackbar.show()
            }
        }

        // Navigation
        listingMetadataViewModel.publishResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess && result.listing != null) {
                listener?.onListingCreated(result.listing)
            } else {
                listener?.onListingCreated(null)
            }
            this.dismiss()
        }
    }

    private fun configureCropActivity() {
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                @Suppress("DEPRECATION")
                val imageUri = result.data?.getParcelableExtra<Uri>("imageUri")
                if (imageUri != null) {
                    listingMetadataViewModel.addImageUri(imageUri)
                }
            }
        }
    }

    private fun getCheckedAmenities(): MutableList<Amenity> {
        val amenities = mutableListOf<Amenity>()
        if (binding.surveillance.isChecked) amenities.add(Amenity.SURVEILLANCE)
        if (binding.climateControlled.isChecked) amenities.add(Amenity.CLIMATE_CONTROLLED)
        if (binding.lighting.isChecked) amenities.add(Amenity.WELL_LIT)
        if (binding.accessibility.isChecked) amenities.add(Amenity.ACCESSIBILITY)
        if (binding.cleanliness.isChecked) amenities.add(Amenity.WEEKLY_CLEANING)
        return amenities
    }
}
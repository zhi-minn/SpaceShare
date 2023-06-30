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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.aemerse.slider.model.CarouselItem
import com.example.spaceshare.CropActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogCreateListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.CreateListingViewModel
import com.example.spaceshare.utils.DecimalInputFilter
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class CreateListingDialogFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: DialogCreateListingBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject
    lateinit var createListingViewModel: CreateListingViewModel
    private var listener: CreateListingViewModel.CreateListingDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ListingDialogStyle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = parentFragment
        if (parentFragment is CreateListingViewModel.CreateListingDialogListener) {
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

        configureButtons()
        configureFilters()
        configureCropActivity()
        configureObservers()
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
            createListingViewModel.decrementSpaceAvailable()
        }
        binding.btnAddSpace.setOnClickListener {
            createListingViewModel.incrementSpaceAvailable()
        }

        // Maps
        binding.btnOpenMaps.setOnClickListener {
            val mapDialogFragment = MapDialogFragment(createListingViewModel, null)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
        }

        // Publish button
        binding.btnPublish.setOnClickListener {
            publishListing(binding.titleTextInput.text.toString(),
                binding.priceInput.text.toString(),
                binding.descriptionTextInput.text.toString())
        }
    }

    private fun configureFilters() {
        binding.priceInput.filters = arrayOf<InputFilter>(DecimalInputFilter)
    }

    private fun configureObservers() {
        // Images
        createListingViewModel.imageUris.observe(viewLifecycleOwner) { uris ->
            binding.carousel.registerLifecycle(lifecycle)
            val list = mutableListOf<CarouselItem>()
            uris.forEach { uri ->
                list.add(CarouselItem(imageUrl = uri.toString()))
            }
            binding.carousel.setData(list)
        }

        // Spaces
        createListingViewModel.spaceAvailable.observe(viewLifecycleOwner) { spaces ->
            binding.spaceText.text = spaces.toString()
        }

        // Location
        createListingViewModel.location.observe(viewLifecycleOwner) { location ->
            binding.parsedLocation.text = GeocoderUtil.getAddress(location.latitude, location.longitude)
        }

        // Navigation
        createListingViewModel.publishResult.observe(viewLifecycleOwner) { result ->
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
                    createListingViewModel.addImageUri(imageUri)
                }
            }
        }
    }

    private fun validateListing(title: String, description: String, price: String): Boolean {
        if (title.isNullOrEmpty() || description.isNullOrEmpty() ||
            price.isNullOrEmpty() || createListingViewModel.location.value == null) {
            Toast.makeText(requireContext(), "Please enter all mandatory details", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun publishListing(title: String, price: String, description: String) {
        val hostId = auth.currentUser?.uid
        if (validateListing(title, description, price)) {
            val listing = Listing(id = UUID.randomUUID().toString(), hostId = hostId,
                title = title, description = description, price = price.toDouble(),
                spaceAvailable = createListingViewModel.spaceAvailable.value ?: 0.0, amenities = getCheckedAmenities())
            createListingViewModel.publishListing(listing)
            binding.scrollView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
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
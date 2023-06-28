package com.example.spaceshare.ui.view

import MapDialogFragment
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
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
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.aemerse.slider.model.CarouselItem
import com.example.spaceshare.CropActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentCreateListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.CreateListingViewModel
import com.example.spaceshare.utils.DecimalInputFilter
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class CreateListingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var permissionDenied = false
    private lateinit var navController: NavController
    private lateinit var binding: FragmentCreateListingBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject
    lateinit var createListingViewModel: CreateListingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_listing, container, false)
        binding.progressBar.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        configureButtons()
        configureFilters()
        configureCropActivity()
        configureObservers()
    }

    private fun configureButtons() {
        // Close button
        binding.btnCloseListing.setOnClickListener {
            navController.popBackStack()
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
            val mapDialogFragment = MapDialogFragment(createListingViewModel)
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
        createListingViewModel.listingPublished.observe(viewLifecycleOwner) {
            navController.popBackStack()
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
                spaceAvailable = createListingViewModel.spaceAvailable.value, amenities = getCheckedAmenities())
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
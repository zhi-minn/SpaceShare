package com.example.spaceshare.ui.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.CropActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentCreateListingBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.CreateListingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateListingFragment : Fragment() {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentCreateListingBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject
    lateinit var createListingViewModel: CreateListingViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        configureButtons()
        configureCropActivity()
        configureObservers()
    }

    private fun configureObservers() {
        createListingViewModel.imageUris.observe(viewLifecycleOwner) { uris ->
            Log.i("tag", "${uris.size} URIS exist here")
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

    private fun configureButtons() {
        // Close button
        binding.btnCloseListing.setOnClickListener {
            navController.popBackStack()
        }

        // Add photo button
        binding.btnAddPhoto.setOnClickListener {
            // startActivity(Intent(requireActivity(), CropActivity::class.java))
            startForResult.launch(Intent(requireActivity(), CropActivity::class.java))
        }

        // Publish button
        binding.btnPublish.setOnClickListener {
            publishListing(binding.titleTextInput.text.toString(),
                binding.priceTextInput.text.toString().toDouble(),
                binding.descriptionTextInput.text.toString())
        }
    }

    private fun publishListing(title: String, price: Double, description: String) {
        val hostID = auth.currentUser?.uid
        val listing = Listing(title = title, price = price, description = description, hostId = hostID)
        createListingViewModel.publishListing(listing)
    }
}
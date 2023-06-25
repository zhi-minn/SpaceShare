package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentListingsBinding
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.CreateListingViewModel
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.utils.ImageAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListingsFragment : Fragment() {

    private lateinit var binding: FragmentListingsBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var listingViewModel: ListingViewModel
    @Inject
    lateinit var createListingViewModel: CreateListingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_listings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        displayListings()
        configureButtons()
    }

    private fun displayListings() {
        listingViewModel.listingsLiveData.observe(viewLifecycleOwner) { listings ->
            binding.listingPage.removeAllViews()
            for (listing in listings) {
                val cardView = layoutInflater.inflate(R.layout.listing_item, null) as CardView
                val viewPager: ViewPager2 = cardView.findViewById(R.id.view_pager_listing_images)
                val title: TextView = cardView.findViewById(R.id.listing_title)
                val description: TextView = cardView.findViewById(R.id.listing_description)
                val price: TextView = cardView.findViewById(R.id.listing_price)

                // Set the listing data to the views
                title.text = listing.title
                description.text = listing.description
                price.text = listing.price.toString()

                // Load the listing image from Firebase Storage into the ImageView
                if (listing.photos != null) {
                    viewPager.adapter = ImageAdapter(listing.photos)
                }

                // Add the CardView to the LinearLayout
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(8, 32, 8, 32)
                cardView.layoutParams = layoutParams
                cardView.radius = 25.0F
                binding.listingPage.addView(cardView)
            }
        }
        listingViewModel.fetchListings(User(FirebaseAuth.getInstance().currentUser?.uid!!))
    }

    private fun configureButtons() {
        binding.btnAddListing.setOnClickListener {
            navController.navigate(R.id.action_listingFragment_to_createListingFragment)
        }
    }

    private fun configureObservers() {
        createListingViewModel.listingPublished.observe(viewLifecycleOwner) { published ->
            if (published) {
                Toast.makeText(requireContext(), "Listing successfully published", Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(requireContext(), "Error publishing listing. Please try again later", Toast.LENGTH_SHORT)
            }
        }
    }
}
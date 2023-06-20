package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.AttachedSurfaceControl
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentListingBinding
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.ListingViewModel
import com.example.spaceshare.utils.ImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListingFragment : Fragment() {

    private lateinit var binding: FragmentListingBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var viewModel: ListingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_listing, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        displayListings()
        configureButtons()
    }

    private fun displayListings() {
        viewModel.listingsLiveData.observe(viewLifecycleOwner) { listings ->
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
                binding.listingPage.addView(cardView)
            }
        }
        viewModel.fetchListings(User("j577YevJRoZHgsKCRC9i1RLACZL2"))
    }

    private fun configureButtons() {
        binding.btnAddListing.setOnClickListener {
            navController.navigate(R.id.action_listingFragment_to_createListingFragment)
        }
    }
}
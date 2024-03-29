package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.R
import com.example.spaceshare.data.repository.ShortlistRepository
import com.example.spaceshare.databinding.ListingItemBinding
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.view.ListingMetadataDialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Objects

class ListingAdapter(
    private val childFragmentManager: FragmentManager,
    private val itemClickListener: ItemClickListener,
    private val shortlistRepo: ShortlistRepository? = null
) : ListAdapter<Listing, ListingAdapter.ViewHolder>(DiffCallback()) {

    var areEditButtonsGone = false
    var areShortlistButtonsGone = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListingItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = getItem(position)
        holder.bind(listing)
    }

    inner class ViewHolder(private val binding: ListingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {
            // Bind the listing data to the views
            binding.title.text = listing.title ?: "Untitled"
            binding.price.text = String.format("$%.2f CAD/day per cubic metre", listing.price)
            binding.spaceAvailable.text = "${listing.spaceAvailable} cubic metres"

            // Load the listing image from Firebase Storage into the ImageView
            binding.viewPagerListingImages.adapter =
                ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
            binding.imageIndicator.setViewPager(binding.viewPagerListingImages)

            // Whether to show the edit button
            binding.btnEdit.isGone = areEditButtonsGone

            // Whether to show the shortlist button
            if (areShortlistButtonsGone) {
                binding.btnShortlist.isGone = areShortlistButtonsGone
            }
            else { // Show shortlist button
                // If shortlistRepo has been passed in, check shortlist logic
                if (shortlistRepo != null) {
                    // Checking what state to set the button to
                    CoroutineScope(Dispatchers.IO).launch {
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                        val isListingInShortlist = shortlistRepo.isListingInShortlist(currentUserId.toString(), listing.id)

                        withContext(Dispatchers.Main) {
                            setShortlistButtonImage(isListingInShortlist)
                        }
                    }

                    // Configure OnClickListener
                    binding.btnShortlist.setOnClickListener {
                        // Change button image state
                        if (it.tag as Int == R.drawable.twotone_star_36) {
                            binding.btnShortlist.setImageResource(R.drawable.twotone_star_selected_36)
                            binding.btnShortlist.setTag(R.drawable.twotone_star_selected_36)
                        }
                        else {
                            binding.btnShortlist.setImageResource(R.drawable.twotone_star_36)
                            binding.btnShortlist.setTag(R.drawable.twotone_star_36)
                        }

                        // Toggle it in the backend
                        CoroutineScope(Dispatchers.IO).launch {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                            shortlistRepo.toggleListingShortlistState(currentUserId.toString(), listing.id)
                        }
                    }
                }
            }

            // Set click listeners
            binding.btnEdit.setOnClickListener {
                println("CLick")
                val listingMetadataDialogFragment = ListingMetadataDialogFragment(listing)
                listingMetadataDialogFragment.show(
                    Objects.requireNonNull(childFragmentManager),
                    "listingMetadataDialog"
                )
            }
            binding.textContainer.setOnClickListener {
                itemClickListener.onItemClick(listing)
            }
        }

        private fun setShortlistButtonImage(isListingInShortlist: Boolean) {
            if (isListingInShortlist) {
                binding.btnShortlist.setImageResource(R.drawable.twotone_star_selected_36)
                binding.btnShortlist.setTag(R.drawable.twotone_star_selected_36)
            }
            else {
                binding.btnShortlist.setImageResource(R.drawable.twotone_star_36)
                binding.btnShortlist.setTag(R.drawable.twotone_star_36)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(listing: Listing)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem == newItem
        }
    }
}

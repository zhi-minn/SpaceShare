package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.databinding.ListingItemBinding
import com.example.spaceshare.models.Listing

class ListingAdapter : ListAdapter<Listing, ListingAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListingItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = getItem(position)
        holder.bind(listing)
    }

    class ViewHolder(private val binding: ListingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing) {
            // Bind the listing data to the views
            binding.listingTitle.text = listing.title

            // Load the listing image from Firebase Storage into the ImageView
            if (listing.photos != null) {
                binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos)
                binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
            }
        }
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

package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.databinding.ListingItemBinding
import com.example.spaceshare.interfaces.ListingAdapterInterface
import com.example.spaceshare.models.Listing

class ListingAdapter(
    private val listingAdapterInterface: ListingAdapterInterface
) : ListAdapter<Listing, ListingAdapter.ViewHolder>(DiffCallback()) {

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListingItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = getItem(position)
        holder.bind(listing)

        holder.getBinding().btnDelete.setOnClickListener {
            val itemPosition = holder.bindingAdapterPosition
            if (itemPosition != RecyclerView.NO_POSITION) {
                val listing = getItem(itemPosition)
                listingAdapterInterface.removeItem(listing, position)
                notifyItemRemoved(position)

                // Adjust remaining items
                notifyItemRangeChanged(position, itemCount - position)
            }
        }
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.Callback() {

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val listing = getItem(position)

                // Perform delete operation for the swiped item
                listingAdapterInterface.removeItem(listing, position)
                notifyItemRemoved(position)

                // Adjust remaining items
                notifyItemRangeChanged(position, itemCount - position)
            }
        }
    }

    class ViewHolder(private val binding: ListingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getBinding(): ListingItemBinding {
            return binding
        }

        fun bind(listing: Listing) {
            // Bind the listing data to the views
            binding.listingTitle.text = listing.title

            // Load the listing image from Firebase Storage into the ImageView
            if (listing.photos != null) {
                binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos)
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

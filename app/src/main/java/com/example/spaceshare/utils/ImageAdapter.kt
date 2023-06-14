package com.example.spaceshare.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.google.firebase.storage.FirebaseStorage

class ImageAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    companion object {
        private val TAG = this::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        val storageRef = FirebaseStorage.getInstance().reference.child("spaces/$imageUrl")
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
            Glide.with(holder.itemView)
                .load(uri)
                .into(holder.image)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error retrieving image: ${e.message}")
            }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.listing_image)
    }
}
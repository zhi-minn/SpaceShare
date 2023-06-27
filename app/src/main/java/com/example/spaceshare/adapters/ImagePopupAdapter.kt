package com.example.spaceshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.storage.FirebaseStorage

class ImagePopupAdapter(
    private val context: Context,
    private val images: List<String>
) : RecyclerView.Adapter<ImagePopupAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_image_popup_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        val storageRef = FirebaseStorage.getInstance().reference.child("spaces/$imageUrl")

        Glide.with(context)
            .load(storageRef)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: PhotoView = itemView.findViewById(R.id.popup_photo_view)
    }
}
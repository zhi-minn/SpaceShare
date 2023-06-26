package com.example.spaceshare.adapters

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.storage.FirebaseStorage

class ImageAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

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

                holder.image.setOnClickListener {
                    showImagePopup(holder.image.context, uri)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error retrieving image: ${e.message}")
            }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun showImagePopup(context: Context, imageUrl: Uri) {
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_image_popup)
        dialog.setCanceledOnTouchOutside(true)

        val photoView = dialog.findViewById<PhotoView>(R.id.popup_photo_view)
        Glide.with(context)
            .load(imageUrl)
            .into(photoView)

        dialog.show()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: PhotoView = itemView.findViewById(R.id.listing_image)
    }
}
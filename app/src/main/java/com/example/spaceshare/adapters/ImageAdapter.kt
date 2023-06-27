package com.example.spaceshare.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.storage.FirebaseStorage
import me.relex.circleindicator.CircleIndicator3

class ImageAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    companion object {
        private val TAG = this::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_image_popup_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        val storageRef = FirebaseStorage.getInstance().reference.child("spaces/$imageUrl")
        try {
            Glide.with(holder.itemView)
                .load(storageRef)
                .into(holder.image)

            holder.image.setOnClickListener {
                showImagePopup(holder.image.context, images, position)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image with reference $imageUrl", e)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun showImagePopup(context: Context, images: List<String>, currentPosition: Int) {
        val dialog = Dialog(context, R.style.ImageDialogStyle)
        dialog.setContentView(R.layout.dialog_image_popup)

        val viewPager = dialog.findViewById<ViewPager2>(R.id.popup_view_pager)
        val adapter = ImagePopupAdapter(context, images)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPosition, false)
        val indicator = dialog.findViewById<CircleIndicator3>(R.id.imageIndicator)
        indicator.setViewPager(viewPager)

        dialog.show()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: PhotoView = itemView.findViewById(R.id.popup_photo_view)
    }
}
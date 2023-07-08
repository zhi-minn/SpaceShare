package com.example.spaceshare.utils

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import com.example.spaceshare.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtil {

    fun showErrorSnackbar(root: View, message: String, resources: Resources) {
        val snackbar = Snackbar.make(root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.error_red, null))
        val tv: TextView =
            snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(resources.getColor(R.color.black, null))
        snackbar.show()
    }
}
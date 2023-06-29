package com.example.spaceshare.utils

import android.content.Context
import android.location.Geocoder
import java.util.Locale

object GeocoderUtil {
    private var geocoder: Geocoder? = null

    fun initialize(context: Context) {
        if (geocoder == null) {
            geocoder = Geocoder(context, Locale.getDefault())
        }
    }

    fun getAddress(latitude: Double, longitude: Double): String {
        geocoder?.let {
            val addresses = it.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append(" ")
                }
                return sb.toString().trim()
            }
        }
        return ""
    }

    fun getGeneralLocation(latitude: Double, longitude: Double): String {
        geocoder?.let {
            val addresses = it.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                return "${address.locality}, ${address.adminArea}, ${address.countryName}"
            }
        }
        return ""
    }
}
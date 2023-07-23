package com.example.spaceshare.utils

import com.google.firebase.firestore.GeoPoint
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

object MathUtil {

    fun calculateDistanceInKilometers(loc1: GeoPoint, loc2: GeoPoint): Double {
        val earthRadiusKm = 6371.0 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)
        val radLat1 = Math.toRadians(loc1.latitude)
        val radLat2 = Math.toRadians(loc2.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(radLat1) * cos(radLat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    fun roundToTwoDecimalPlaces(number: Double): Double {
        val bd = BigDecimal(number).setScale(2, RoundingMode.HALF_EVEN)
        return bd.toDouble()
    }
}
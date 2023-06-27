package com.example.spaceshare.models

import com.google.firebase.firestore.GeoPoint

data class Listing(
    var id: String? = null,
    val hostId: String? = null,
    val photos: MutableList<String> = mutableListOf(),
    val title: String? = null,
    val price: Double = 0.00,
    val description: String? = null,
    val spaceAvailable: Double = 0.0,
    // var active: Boolean = true,
    // val ratings: List<Rating>? = null
) {
    var location: GeoPoint? = null
}
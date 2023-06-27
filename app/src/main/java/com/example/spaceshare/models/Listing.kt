package com.example.spaceshare.models

import com.google.firebase.firestore.GeoPoint

data class Listing(
    var id: String? = null,
    val hostId: String? = null,
    val photos: MutableList<String> = mutableListOf(),
    var title: String? = "",
    var price: Double? = 0.00,
    var description: String? = "",
    var spaceAvailable: Double? = 0.0
    // val availability: Availability? = null,
    // val amenities: List<String>? = null,
    // val ratings: List<Rating>? = null
) {
    var location: GeoPoint? = null
}
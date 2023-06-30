package com.example.spaceshare.models

import com.example.spaceshare.enums.Amenity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class Listing(
    var id: String = UUID.randomUUID().toString(),
    val hostId: String? = null,
    val photos: MutableList<String> = mutableListOf(),
    var title: String = "",
    var price: Double = 0.00,
    var description: String = "",
    var spaceAvailable: Double = 0.0,
    val amenities: MutableList<Amenity> = mutableListOf(),
    var isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    var updatedAt: Timestamp? = null
    // val availability: Availability? = null,
    // val ratings: List<Rating>? = null
) {
    var location: GeoPoint? = null
}
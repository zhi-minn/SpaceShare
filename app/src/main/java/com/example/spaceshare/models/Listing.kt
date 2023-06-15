package com.example.spaceshare.models

import java.util.Date

data class Listing(
    val id: String? = null,
    val hostId: String? = null,
    val photos: List<String>? = null,
    val title: String? = null,
    val price: Double = 0.00,
    val description: String? = null,
    val location: String = "",
    // val availability: Availability? = null,
    // val size: String? = null,

    // val amenities: List<String>? = null,

    // val ratings: List<Rating>? = null
) {

}
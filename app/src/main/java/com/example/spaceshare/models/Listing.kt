package com.example.spaceshare.models

import java.util.Date

data class Listing(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val location: String = "",
    val price: Double = 0.00,
    // val availability: Availability? = null,
    // val size: String? = null,
    val photos: List<String>? = null,
    // val amenities: List<String>? = null,
    // val host: Host? = null,
    // val ratings: List<Rating>? = null
) {
        data class Availability(
            val startDate: Date? = null,
            val endDate: Date? = null
        )

        data class Host(
            val name: String? = null,
            val contactInfo: String? = null,
            val profilePicture: String? = null
        )

        data class Rating(
            val rating: Double? = null,
            val review: String? = null
        )
}

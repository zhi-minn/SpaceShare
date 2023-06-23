package com.example.spaceshare.models

import com.google.firebase.firestore.GeoPoint

data class Preferences(
    val userId: String? = null
) {
    var location: GeoPoint? = null
    var radius: Int = 5
}
package com.example.spaceshare.models

import com.google.firebase.firestore.GeoPoint

data class Preferences(
    val userId: String? = null,
    val email: String? = null,
    var isActive: Boolean = true,
    var location: GeoPoint? = null,
    var radius: Int = 5
) {}
package com.example.spaceshare.models
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class SearchCriteria (
    val spaceRequired: Double = 0.0,
    val location: GeoPoint,
    val startDate: Timestamp,
    val endDate: Timestamp
)
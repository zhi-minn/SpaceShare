package com.example.spaceshare.models

import com.google.firebase.Timestamp

data class Booking(
    val startDate : Timestamp? = null,
    val endDate : Timestamp? = null,
    val reservedSpace : Double = 0.0
)

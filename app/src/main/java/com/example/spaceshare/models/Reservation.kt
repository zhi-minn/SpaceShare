package com.example.spaceshare.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

enum class ReservationStatus {
    PENDING,
    APPROVED,
    DECLINED,
    CANCELLED,
    COMPLETED
}

inline fun <reified T : Enum<T>> Int.toEnum(): T? {
    return enumValues<T>().firstOrNull { it.ordinal == this }
}

inline fun <reified T : Enum<T>> T.toInt(): Int {
    return this.ordinal
}

data class Reservation(
    val id: String? = null,
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val totalCost: Double = 0.0,
    @ServerTimestamp
    val startDate: Timestamp? = null,
    @ServerTimestamp
    val endDate: Timestamp? = null,
    val spaceRequested: Double = 0.0,
    val status: ReservationStatus = ReservationStatus.PENDING,
    val location: String = "",
    val listingTitle: String = "",
    val previewPhoto: String? = null,
    val clientFirstName: String? = null,
    val clientLastName: String? = null,
    val clientPhoto: String? = null,
//    val rating:Int? = null
) {
}
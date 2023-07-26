package com.example.spaceshare.models

import com.example.spaceshare.enums.DeclareItemType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

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
    val id: String = UUID.randomUUID().toString(),
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val totalCost: Double = 0.0,
    @ServerTimestamp
    val startDate: Timestamp? = null,
    @ServerTimestamp
    val endDate: Timestamp? = null,
    val spaceRequested: Double = 0.0,
    var status: ReservationStatus = ReservationStatus.PENDING,
    val location: String = "",
    val listingTitle: String = "",
    val previewPhoto: String? = null,
    val clientFirstName: String? = null,
    val clientLastName: String? = null,
    val clientPhoto: String? = null,
    val message: String? = null,
    var rated:Boolean = false, // likes will only goes up when user click thumbs_up when rating,clicking thumbs_down won't do anything
    val items: Map<String, String>? = null
//    val rating:Int? = null
) {
}
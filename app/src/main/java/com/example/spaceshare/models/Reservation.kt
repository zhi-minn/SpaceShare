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
    @ServerTimestamp
    val startDate: Timestamp? = null,
    @ServerTimestamp
    val endDate: Timestamp? = null,
    val unit: Double? = null,
    val status: Int? = null

//    val rating:Int? = null
) {

}
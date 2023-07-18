package com.example.spaceshare.models

import com.google.firebase.Timestamp

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
    val startDate: Timestamp? = Timestamp.now(),
    val endDate: Timestamp? = null,
    val unit: Int? = null,
    val status: Int? = null

//    val rating:Int? = null
) {

}
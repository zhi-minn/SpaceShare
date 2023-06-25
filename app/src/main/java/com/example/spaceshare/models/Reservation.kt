package com.example.spaceshare.models

import com.google.firebase.Timestamp

enum class ReservationStatus(i: Int) {
    PENDING(0),
    APPROVED(1),
    DECLINED(2),
    COMPLETED(3),
    CANCELLED(4)
}
data class Reservation(
    val id: String? = null,
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val unit: Int? = null,
    val status: Int? = null

//    val rating:Int? = null
) {

}
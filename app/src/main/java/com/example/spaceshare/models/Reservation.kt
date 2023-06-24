package com.example.spaceshare.models

import com.google.firebase.Timestamp

data class Reservation(
    val id: String? = null,
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val unit: Int? = null,
    val isPending: Boolean? = true,
    val isApproved: Boolean? = false,

//    val rating:Int? = null
) {

}
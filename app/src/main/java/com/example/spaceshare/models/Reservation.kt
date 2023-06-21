package com.example.spaceshare.models

import java.time.LocalDate

data class Reservation(
    val id: String? = null,
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val unit: Int? = null,
    val isPending: Boolean? = true,
    val isApproved: Boolean? = false,

//    val rating:Int? = null
) {

}
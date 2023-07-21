package com.example.spaceshare.models

import com.example.spaceshare.consts.ListingConsts
import com.example.spaceshare.enums.Amenity

data class FilterCriteria(
    val isActive: Boolean = true,
    val isInactive: Boolean = true,
    val minPrice: Float = 0.0f,
    var maxPrice: Float = ListingConsts.DEFAULT_MAX_PRICE,
    val minSpace: Float = ListingConsts.SPACE_BOOKING_LOWER_LIMIT.toFloat(),
    var maxSpace: Float = ListingConsts.SPACE_UPPER_LIMIT.toFloat(),
    val amenities: MutableList<Amenity> = mutableListOf()
)
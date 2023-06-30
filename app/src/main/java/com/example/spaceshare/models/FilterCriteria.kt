package com.example.spaceshare.models

import com.example.spaceshare.consts.ListingConsts

data class FilterCriteria(
    val isActive: Boolean = true,
    val isInactive: Boolean = true,
    val minPrice: Float = 0.0f,
    val maxPrice: Float = 100.0f,
    val minSpace: Float = ListingConsts.SPACE_BOOKING_LOWER_LIMIT.toFloat(),
    val maxSpace: Float = ListingConsts.SPACE_UPPER_LIMIT.toFloat()
)
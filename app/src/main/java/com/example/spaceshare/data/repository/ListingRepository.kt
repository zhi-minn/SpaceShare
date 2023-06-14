package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Listing

interface ListingRepository {

    fun createListing(listing: Listing) : String
}
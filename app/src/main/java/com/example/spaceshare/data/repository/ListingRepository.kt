package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria

interface ListingRepository {

    suspend fun postListing(listing: Listing) : String

    suspend fun getUserListings(userId: String): List<Listing>

    suspend fun getAllListings(): List<Listing>

    suspend fun searchListings(criteria: SearchCriteria): List<Listing>

    suspend fun getListing(listingId: String): Listing?

    suspend fun updateListing(listing: Listing): Boolean

    suspend fun deleteListing(listingId: String)
}
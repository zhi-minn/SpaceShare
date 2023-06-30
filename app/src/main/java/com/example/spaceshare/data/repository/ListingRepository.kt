package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.models.User

interface ListingRepository {

    suspend fun createListing(listing: Listing) : String

    suspend fun getUserListings(user: User): List<Listing>

    suspend fun getAllListings(): List<Listing>

    suspend fun searchListings(criteria: SearchCriteria): List<Listing>

    suspend fun getListing(listingId: String): Listing?

    suspend fun updateListing(listing: Listing): Boolean

    suspend fun deleteListing(listingId: String)
}
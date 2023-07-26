package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Shortlist

interface ShortlistRepository {

    suspend fun getShortlist(userId: String): Shortlist?

    suspend fun getShortlistedListings(userId: String): List<Listing>

    suspend fun updateShortlist(shortlist: Shortlist)

    suspend fun isListingInShortlist(userId: String, listingId: String): Boolean

    suspend fun addListingToShortlist(userId: String, listingId: String)

    suspend fun removeListingFromShortlist(userId: String, listingId: String)

    suspend fun toggleListingShortlistState(userId: String, listingId: String)
}
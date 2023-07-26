package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.ShortlistRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Shortlist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShortlistRepoImpl @Inject constructor(
    db: FirebaseFirestore,
    private val listingRepo: ListingRepository
): ShortlistRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val shortlistsCollection = db.collection("shortlists")

    override suspend fun getShortlist(userId: String): Shortlist? = withContext(Dispatchers.IO) {
        try {
            val result = shortlistsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            return@withContext result.documents.firstOrNull()?.toObject(Shortlist::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading shortlist document: ${e.message}")
            return@withContext null
        }
    }

    override suspend fun getShortlistedListings(userId: String): List<Listing> = withContext(Dispatchers.IO) {
        try {
            val shortlist = getShortlist(userId)

            val shortlistedListings = mutableListOf<Listing>()

            shortlist?.listingIds?.forEach { listingId ->
                val listing = listingRepo.getListing(listingId)
                if (listing != null) {
                    shortlistedListings.add(listing)
                }
            }

            return@withContext shortlistedListings
        } catch (e: Exception) {
            Log.e(TAG, "Error reading shortlist document: ${e.message}")
            return@withContext emptyList<Listing>()
        }
    }

    override suspend fun updateShortlist(shortlist: Shortlist) {
        TODO("Not yet implemented")
    }

}
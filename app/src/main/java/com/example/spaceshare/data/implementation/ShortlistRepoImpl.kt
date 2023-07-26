package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.ShortlistRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Shortlist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShortlistRepoImpl @Inject constructor(
    db: FirebaseFirestore,
    private val listingRepo: ListingRepository
) : ShortlistRepository {

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

    override suspend fun getShortlistedListings(userId: String): List<Listing> =
        withContext(Dispatchers.IO) {
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
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Shortlist>()
            shortlist.userId?.let {
                shortlistsCollection.document(shortlist.userId)
                    .set(shortlist)
                    .addOnSuccessListener {
                        Log.d(TAG, "Updated shortlist to $shortlist")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to update shortlist: ${e.message}")
                        deferred.completeExceptionally(e)
                    }
            }
            deferred.await()
        }
    }

    override suspend fun isListingInShortlist(userId: String, listingId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val shortlist = getShortlist(userId)

                if (shortlist != null) {
                    return@withContext shortlist.listingIds.contains(listingId)
                }

                return@withContext false
            } catch (e: Exception) {
                Log.e(TAG, "Error reading shortlist listing ids of user $userId: ${e.message}")
                return@withContext false
            }
        }

    override suspend fun addListingToShortlist(userId: String, listingId: String) {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Shortlist>()
            try {
                val shortlist = getShortlist(userId)

                // If there's already a shortlist, add to it
                if (shortlist != null) {
                    shortlist.listingIds.add(listingId)
                    updateShortlist(shortlist)
                }
                // Otherwise make a new shortlist object for the user
                else {
                    val newShortlist = Shortlist(userId, mutableListOf(listingId))
                    shortlistsCollection.add(newShortlist)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding listingId $listingId to shortlist of user $userId: ${e.message}")
                deferred.completeExceptionally(e)
            }
            deferred.await()
        }
    }

    override suspend fun removeListingFromShortlist(userId: String, listingId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val shortlist = getShortlist(userId)
                if (shortlist != null) {
                    shortlist.listingIds.remove(listingId)
                    updateShortlist(shortlist)
                }
            }
            catch (e: Exception) {
                Log.e(TAG, "Error removing listingId $listingId from shortlist of user $userId: ${e.message}")
            }
        }
    }

    override suspend fun toggleListingShortlistState(userId: String, listingId: String) {
        return withContext(Dispatchers.IO) {
            val isListingInShortlist = isListingInShortlist(userId, listingId)
            if (isListingInShortlist) {
                removeListingFromShortlist(userId, listingId)
            }
            else {
                addListingToShortlist(userId, listingId)
            }
        }
    }
}
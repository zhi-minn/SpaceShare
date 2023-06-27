package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.models.User
import com.example.spaceshare.utils.MathUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListingRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ListingRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val listingsCollection = db.collection("listings")
    override suspend fun createListing(listing: Listing): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()
            listingsCollection.document(listing.id!!)
                .set(listing)
                .addOnSuccessListener {
                    Log.d("listings", "Added listing with id ${listing.id}")
                    deferred.complete(listing.id!!)
                }
                .addOnFailureListener { e ->
                    Log.w("listings", "Error adding document", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }

    }

    override suspend fun fetchListings(user: User): List<Listing> = withContext(Dispatchers.IO) {
        try {
            val result = listingsCollection
                .whereEqualTo("hostId", user.id)
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    val listing = document.toObject(Listing::class.java)
                    listing?.id = document.id
                    listing
                } catch (e: Exception) {
                    Log.e(TAG, "Error casting document to Listing object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading listings document: ${e.message}")
            return@withContext emptyList()
        }
    }

    override suspend fun searchListings(criteria: SearchCriteria): List<Listing> =
        withContext(Dispatchers.IO) {
            try {
                val queryResult = listingsCollection
                        // TODO: Filter for space available being greater than spaceRequired
                        // TODO: Filter for available dates (need reservations to be finished)
                    .get()
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error getting documents: ", exception)
                    }
                    .await()

                val castedToListings = queryResult.documents.mapNotNull { document ->
                    try {
                        val listing = document.toObject(Listing::class.java)
                        listing?.id = document.id
                        listing
                    } catch (e: Exception) {
                        Log.e(TAG, "Error casting document to Listing object: ${e.message}")
                        null
                    }
                }
                val sortedByDistance = castedToListings.sortedBy { listing ->
                    try {
                        val dist = MathUtil.calculateDistanceInKilometers(
                            criteria.location,
                            listing.location!!
                        )
                        dist
                    } catch (e: Exception) {
                        Log.e(TAG, "Error calculating Listing distance for id ${listing.id}: ${e.message}")
                        null
                    }
                }

                return@withContext sortedByDistance

            } catch (e: Exception) {
                Log.e(TAG, "Error searching for listings with criteria $criteria: ${e.message}")
                return@withContext emptyList()
            }
        }

    override suspend fun deleteListing(listingId: String): Unit = withContext(Dispatchers.IO) {
        try {
            listingsCollection.document(listingId).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting listing with ID $listingId: ${e.message}", e)
        }
    }
}
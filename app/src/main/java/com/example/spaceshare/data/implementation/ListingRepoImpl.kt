package com.example.spaceshare.data.implementation

import android.util.Log
import androidx.compose.runtime.rememberUpdatedState
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.models.User
import com.example.spaceshare.utils.MathUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListingRepoImpl @Inject constructor(
    db: FirebaseFirestore
) : ListingRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val listingsCollection = db.collection("listings")
    override suspend fun setListing(listing: Listing): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()
            listingsCollection.document(listing.id)
                .set(listing)
                .addOnSuccessListener {
                    Log.d(TAG, "Set listing with id ${listing.id}")
                    deferred.complete(listing.id)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error setting document", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }

    }

    override suspend fun getUserListings(userId: String): List<Listing> = withContext(Dispatchers.IO) {
        try {
            val result = listingsCollection
                .whereEqualTo("hostId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
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

    override suspend fun getAllListings(): List<Listing> = withContext(Dispatchers.IO) {
        try {
            val result = listingsCollection
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
                    .whereGreaterThanOrEqualTo("spaceAvailable", criteria.spaceRequired)
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

                val filteredByDistance = castedToListings.filter { listing ->
                    try {
                        val dist = MathUtil.calculateDistanceInKilometers(
                            criteria.location,
                            listing.location!!
                        )
                        dist <= criteria.radius
                    } catch (e: Exception) {
                        Log.e(TAG, "Error calculating and filtering by Listing distance for id ${listing.id}: ${e.message}")
                        false
                    }
                }

                val sortedByDistance = filteredByDistance.sortedBy { listing ->
                    try {
                        val dist = MathUtil.calculateDistanceInKilometers(
                            criteria.location,
                            listing.location!!
                        )
                        dist
                    } catch (e: Exception) {
                        Log.e(TAG, "Error calculating and sorting by Listing distance for id ${listing.id}: ${e.message}")
                        null
                    }
                }

                return@withContext sortedByDistance

            } catch (e: Exception) {
                Log.e(TAG, "Error searching for listings with criteria $criteria: ${e.message}")
                return@withContext emptyList()
            }
        }

    override suspend fun getListing(listingId: String): Listing? {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Listing?>()
            listingsCollection.document(listingId)
                .get()
                .addOnSuccessListener {
                    try {
                        val listing = it.toObject(Listing::class.java)
                        deferred.complete(listing)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error casting listing with ID $listingId to Listing object: ${e.message}", e)
                        deferred.complete(null)
                    }
                }
            deferred.await()
        }
    }

    override suspend fun updateListing(listing: Listing): Boolean = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<Boolean>()
        listingsCollection.document(listing.id)
            .set(listing)
            .addOnSuccessListener {
                Log.i(TAG, "Update success for listing with id ${listing.id}")
                deferred.complete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update listing: ${e.message}", e)
                deferred.complete(false)
            }
        deferred.await()
    }

    override suspend fun deleteListing(listingId: String): Unit = withContext(Dispatchers.IO) {
        try {
            listingsCollection.document(listingId).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting listing with ID $listingId: ${e.message}", e)
        }
    }
}
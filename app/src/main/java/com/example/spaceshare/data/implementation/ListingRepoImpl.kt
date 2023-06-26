package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListingRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
): ListingRepository {

    companion object {
        private val TAG = this::class.simpleName
    }
    private val listingsCollection = db.collection("listings")
    override suspend fun createListing(listing: Listing): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()
            listingsCollection.add(listing)
                .addOnSuccessListener { documentReference ->
                    Log.d("listings", "Added listing with id ${documentReference.id}")
                    deferred.complete(documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w("listings", "Error adding document", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }

    }

    override suspend fun fetchListings(user: User): List<Listing> = withContext(Dispatchers.IO){
        try {
            val result = listingsCollection
                .whereEqualTo("hostId", user.id)
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    document.toObject(Listing::class.java)
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

    override suspend fun searchListings() {

    }
}
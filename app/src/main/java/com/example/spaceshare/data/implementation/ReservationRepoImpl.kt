package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReservationRepoImpl @Inject constructor(
    db: FirebaseFirestore
): ReservationRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val reservationsCollection = db.collection("reservations")
    private val listingsCollection = db.collection("listings")
    override suspend fun createReservation(reservation: Reservation): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()
            reservationsCollection.add(reservation)
                .addOnSuccessListener { documentReference ->
                    Log.d("reservations", "Added reservation with id ${documentReference.id}")
                    deferred.complete(documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w("reservations", "Error adding document", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }
    }

    override suspend fun fetchReservations(user: User, asHost: Boolean): List<Reservation> =
        withContext(Dispatchers.IO) {
            try {
                val field = if (asHost) "hostId" else "clientId"
                val result = reservationsCollection
                    .whereEqualTo(field, user.id)
                    .get()
                    .await()

                return@withContext result.documents.mapNotNull { document ->
                    try {
                        document.toObject(Reservation::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error casting document to Reservation object: ${e.message}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading Reservations document: ${e.message}")
                return@withContext emptyList()
            }
        }

//    override suspend fun fetchListings(reservations: List<Reservation>?): List<Listing> {
//        val listingIds = reservations?.map { i -> i.listingId }
//        val tasks = mutableListOf<Task<Listing>>()
//        if (listingIds != null) {
//
//            for (id in listingIds) {
//                if (id == null) continue
//                val documentRef = listingsCollection.document(id)
//                val task = documentRef.get().continueWith { documentSnapshot ->
//                    val listing: Listing? = documentSnapshot.result?.toObject(Listing::class.java)
//                    listing ?: throw Exception("listing not found")
//                }
//                tasks.add(task)
//            }
//        }
//
//        return Tasks.whenAllSuccess<Listing>(tasks).await()
//    }
}
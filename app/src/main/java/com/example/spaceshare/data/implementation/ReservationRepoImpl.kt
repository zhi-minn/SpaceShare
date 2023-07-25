package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.models.Booking
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.spaceshare.models.ReservationStatus.APPROVED

class ReservationRepoImpl @Inject constructor(
    db: FirebaseFirestore,
    private val listingRepo: ListingRepository
): ReservationRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val reservationsCollection = db.collection("reservations")
    private val listingsCollection = db.collection("listings")
    private val userCollection = db.collection("users")

    override suspend fun createReservation(reservation: Reservation): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()

            // Add the reservation request to the list of bookings for the listing
            val listing = listingRepo.getListing(reservation.listingId.toString())
            if (listing != null) {
                val newBooking = Booking(reservation.startDate, reservation.endDate, reservation.spaceRequested)
                listing.bookings.add(newBooking)
                listingRepo.updateListing(listing)
            }

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

    override suspend fun fetchCompletedReservationsByListing(listingId: String): List<Reservation> {
        return withContext(Dispatchers.IO) {
            try {
                val result = reservationsCollection
                    .whereEqualTo("listingId", listingId)
                    .whereEqualTo("status", APPROVED)
                    .whereLessThan("endDate", Timestamp.now())
                    .get()
                    .await()

                return@withContext result.documents.mapNotNull { document ->
                    try {
                        document.toObject(Reservation::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error casting document to Reservation object: ${e.message}", e)
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving reservations by listing id $listingId: ${e.message}", e)
                return@withContext emptyList()
            }
        }
    }

    override suspend fun fetchUser(id : String): User? =
        withContext(Dispatchers.IO) {
            try {
                val result = userCollection
                    .whereEqualTo("id", id)
                    .get()
                    .await()

                try {
                    val document = result.documents[0]
                    return@withContext document.toObject(User::class.java)
                } catch (e: Exception) {
                    Log.e(TAG, "Error casting document to User object: ${e.message}")
                    null
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching User info: ${e.message}")
                return@withContext null
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
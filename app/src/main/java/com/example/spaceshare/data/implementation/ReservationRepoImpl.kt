package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.models.Booking
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.ReservationStatus.APPROVED
import com.example.spaceshare.models.ReservationStatus.PENDING
import com.example.spaceshare.models.User
import com.example.spaceshare.models.Chat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.google.firebase.database.ktx.database
import java.util.Calendar
import com.google.firebase.ktx.Firebase

class ReservationRepoImpl @Inject constructor(
    db: FirebaseFirestore,
    private val listingRepo: ListingRepository
): ReservationRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val realTimeDB = Firebase.database
    private val baseAvailableSpaceRef = realTimeDB.reference.child("currentSpaceAvailable")


    private val reservationsCollection = db.collection("reservations")
    private val userCollection = db.collection("users")
    private val chatCollection = db.collection("chats")

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

            reservationsCollection
                .document(reservation.id)
                .set(reservation)
                .addOnSuccessListener { documentReference ->
                    Log.d("reservations", "Added reservation with id ${reservation.id}")
                    deferred.complete(reservation.id)
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

    override suspend fun fetchUpcomingReservationByListingId(listingId: String): List<Reservation> {
        return withContext(Dispatchers.IO) {
            try {
                val result = reservationsCollection
                    .whereEqualTo("listingId", listingId)
                    .whereIn("status", listOf(PENDING, APPROVED))
                    .whereGreaterThan("endDate", Timestamp.now())
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
                Log.e(TAG, "Error retrieving upcoming reservations by listing id $listingId: ${e.message}", e)
                return@withContext emptyList()
            }
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
                Log.e(TAG, "Error retrieving completed reservations by listing id $listingId: ${e.message}", e)
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

    override suspend fun setChat(chat: Chat): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()

            chatCollection.add(chat)
                .addOnSuccessListener { documentReference ->
                    Log.d("chats", "Added Chat with id ${documentReference.id}")
                    deferred.complete(documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w("chats", "Error adding Chat", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }
    }

    override suspend fun setReservationStatus(reservation : Reservation, status : ReservationStatus) {
        val deferred = CompletableDeferred<Boolean>()
        reservation.status = status
        reservationsCollection.document(reservation.id)
            .set(reservation)
            .addOnSuccessListener {
                Log.i(TAG, "Update success for Reservation with id ${reservation.id}")
                deferred.complete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update Reservation: ${e.message}", e)
                deferred.complete(false)
            }
        deferred.await()
    }

    private fun getLongDateFromTimestamp(timestamp: Timestamp): Long {
        val date = timestamp.toDate()

        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return year * 10000L + month * 100L + day
    }

    override suspend fun getAvailableSpace(listing : Listing, startDate : Long, endDate : Long) : Double {
//        val startDateLong = getLongDateFromTimestamp(startDate)
//        val endDateLong = getLongDateFromTimestamp(endDate)

        val query = baseAvailableSpaceRef.child(listing.id).get().await()

        var spaceAvailable : Double = 0.0

        val data = query?.value as? Map<String, String>

        if (data != null) {
            val filteredData = data.filterKeys { date ->
                val longDate = date.toLong()
                longDate in startDate..endDate
            }

            // Get the minimum space within the date range
            var minSpace : Double = listing.spaceAvailable
            if (filteredData.isNotEmpty()) {
                minSpace = filteredData.minByOrNull { it.value.toDouble() }?.value?.toDouble()!!
            }

            spaceAvailable = minSpace!!
        } else {
            // if the given dates are not booked
            spaceAvailable = listing.spaceAvailable
        }

        return spaceAvailable
    }
    private fun getDateRange(startDate: Long, endDate: Long): List<String> {
        val dates = mutableListOf<String>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            dates.add(currentDate.toString())
            currentDate += (24 * 60 * 60 * 1000) // Increment by one day in milliseconds
        }
        return dates
    }

    override suspend fun reserveSpace(unit : Double, listing : Listing, startDate : Long, endDate : Long) : Boolean {
        val currentSpace = getAvailableSpace(listing, startDate, endDate)

//        val startDateLong = getLongDateFromTimestamp(startDate)
//        val endDateLong = getLongDateFromTimestamp(endDate)
        val dateRange = getDateRange(startDate, endDate)

        if (currentSpace < unit) {
            return false
        }

        val query = baseAvailableSpaceRef.child(listing.id)

        return try {
            val snapshot = query.get().await()
            val data = snapshot.value as? Map<String, String>

            for (date in dateRange) {
                val remainingSpace = ((data?.get(date)?.toDouble() ?: listing.spaceAvailable) - unit).toString()
                baseAvailableSpaceRef.child(listing.id).child(date).setValue(remainingSpace)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error when reserving space in db", e)
            false
        }
    }

    override suspend fun cancelSpace(unit : Double, listing : Listing, startDate : Long, endDate : Long) : Boolean {
//        val startDateLong = getLongDateFromTimestamp(startDate)
//        val endDateLong = getLongDateFromTimestamp(endDate)
        val dateRange = getDateRange(startDate, endDate)

        val query = baseAvailableSpaceRef.child(listing.id)

        return try {
            val snapshot = query.get().await()
            val data = snapshot.value as? Map<String, String>

            for (date in dateRange) {
                val remainingSpace = ((data?.get(date)?.toDouble() ?: 0.0) + unit).toString()
                baseAvailableSpaceRef.child(listing.id).child(date).setValue(remainingSpace)
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error when returning space in db", e)
            false
        }
    }



    override suspend fun setReservationRated(reservation: Reservation, rated: Boolean) {
        val deferred = CompletableDeferred<Boolean>()
        reservation.rated = rated
        reservationsCollection.document(reservation.id)
            .set(reservation)
            .addOnSuccessListener {
                Log.i(TAG, "Update success for Reservation with id ${reservation.id}")
                deferred.complete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update Reservation: ${e.message}", e)
                deferred.complete(false)
            }
        deferred.await()
    }

    override suspend fun setReservationPaid(reservation: Reservation, paid: Boolean) {
        val deferred = CompletableDeferred<Boolean>()
        reservation.paymentCompleted = paid
        reservationsCollection.document(reservation.id)
            .set(reservation)
            .addOnSuccessListener {
                Log.i(TAG, "Update paid attribute successfully for Reservation with id ${reservation.id}")
                deferred.complete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update Reservation: ${e.message}", e)
                deferred.complete(false)
            }
        deferred.await()
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
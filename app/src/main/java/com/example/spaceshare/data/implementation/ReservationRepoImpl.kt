package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReservationRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
): ReservationRepository {

    companion object {
        private val TAG = this::class.simpleName
    }
    private val ReservationsCollection = db.collection("reservations")
    override fun createReservation(Reservation: Reservation): String {
        var newReservationID = ""
        ReservationsCollection.add(Reservation)
            .addOnSuccessListener { documentReference ->
                Log.d("reservations", "Added Reservation with id ${documentReference.id}")
                newReservationID = documentReference.id
            }
            .addOnFailureListener { e ->
                Log.w("reservations", "Error adding document", e)
            }

        return newReservationID
    }

    override suspend fun fetchReservations(user: User, asClient: Boolean): List<Reservation> = withContext(Dispatchers.IO){
        try {
            val field = if (asClient) "clientId" else "hostId"
            val result = ReservationsCollection
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
}
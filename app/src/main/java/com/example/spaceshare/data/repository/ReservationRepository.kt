package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.google.android.gms.tasks.Task

interface ReservationRepository {

    suspend fun createReservation(Reservation: Reservation) : String

    suspend fun fetchReservations(user: User, asHost: Boolean): List<Reservation>

    suspend fun fetchCompletedReservationsByListing(listingId: String): List<Reservation>

    suspend fun fetchUser(id : String) : User?

    suspend fun setChat(chat : Chat) : String
}
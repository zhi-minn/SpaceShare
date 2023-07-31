package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.User

interface ReservationRepository {

    suspend fun createReservation(Reservation: Reservation) : String

    suspend fun fetchReservations(user: User, asHost: Boolean): List<Reservation>

    suspend fun fetchUpcomingReservationByListingId(listingId: String): List<Reservation>
    suspend fun fetchCompletedReservationsByListing(listingId: String): List<Reservation>

    suspend fun fetchUser(id : String) : User?

    suspend fun setChat(chat : Chat) : String

    suspend fun setReservationStatus(reservation : Reservation, status : ReservationStatus)
    suspend fun setReservationRated(reservation: Reservation, rated: Boolean)

    suspend fun setReservationPaid(reservation: Reservation, paid: Boolean)

    suspend fun getAvailableSpace(listing : Listing, startDate : Long, endDate : Long) : Double

    suspend fun reserveSpace(unit : Double, listing : Listing, startDate : Long, endDate : Long) : Boolean

    suspend fun cancelSpace(unit : Double, listing : Listing, startDate : Long, endDate : Long) : Boolean
}
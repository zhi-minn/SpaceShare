package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User

interface ReservationRepository {

    fun createReservation(Reservation: Reservation) : String

    suspend fun fetchReservations(user: User, asClient: Boolean): List<Reservation>
}
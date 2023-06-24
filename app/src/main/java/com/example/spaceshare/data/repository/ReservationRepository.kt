package com.example.spaceshare.data.repository

import com.example.spaceshare.data.implementation.ReservationRepoImpl
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

interface ReservationRepository {

    fun createReservation(Reservation: Reservation) : String

    suspend fun fetchReservations(user: User, asHost: Boolean): List<Reservation>
}
package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.ReservationRepoImpl
import com.example.spaceshare.data.repository.ReservationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class ReservationModule {

    @Binds
    abstract fun bindReservationRepository(
        ReservationRepoImpl: ReservationRepoImpl
    ): ReservationRepository

}
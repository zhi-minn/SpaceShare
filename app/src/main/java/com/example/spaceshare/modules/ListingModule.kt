package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.ListingRepoImpl
import com.example.spaceshare.data.repository.ListingRepository
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
abstract class ListingModule {

    @Binds
    abstract fun bindListingRepository(
        listingRepoImpl: ListingRepoImpl
    ): ListingRepository

    companion object {
        @Provides
        fun provideFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }
}
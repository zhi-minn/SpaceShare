package com.example.spaceshare.data.repository

import com.example.spaceshare.data.implementation.ListingRepoImpl
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

interface ListingRepository {

    fun createListing(listing: Listing)

    suspend fun fetchListings(user: User): List<Listing>
}

@Module
@InstallIn(FragmentComponent::class)
abstract class ListingRepositoryBindsModule {

    @Binds
    abstract fun bindListingRepository(
        listingRepoImpl: ListingRepoImpl
    ): ListingRepository
}

@Module
@InstallIn(FragmentComponent::class)
object ListingRepositoryProvidesModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}
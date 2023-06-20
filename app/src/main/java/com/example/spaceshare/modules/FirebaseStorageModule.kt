package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.FirebaseStorageRepoImpl
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class FirebaseStorageModule {

    @Binds
    abstract fun bindFirebaseStorageRepository(
        firebaseStorageRepoImpl: FirebaseStorageRepoImpl
    ): FirebaseStorageRepository

    companion object {
        @Provides
        fun provideFirebaseStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }
    }
}
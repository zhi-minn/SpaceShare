package com.example.spaceshare.modules
import com.example.spaceshare.data.implementation.SearchRepoImpl
import com.example.spaceshare.data.repository.SearchRepository
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
abstract class SearchModule {

    @Binds
    abstract fun bindSearchRepository(
       searchRepoImpl: SearchRepoImpl
    ): SearchRepository

    companion object {
        @Provides
        fun provideFireStore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }
}
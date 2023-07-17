package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.MessagesRepoImpl
import com.example.spaceshare.data.repository.MessagesRepository
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
abstract class MessagesModule {

    @Binds
    abstract fun bindMessageRepository(
        messagesRepoImpl: MessagesRepoImpl
    ): MessagesRepository
}
package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.ShortlistRepoImpl
import com.example.spaceshare.data.repository.ShortlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class ShortlistModule {

    @Binds
    abstract fun bindShortlistRepository(
        shortlistRepoImpl: ShortlistRepoImpl
    ): ShortlistRepository
}
package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.PreferencesRepoImpl
import com.example.spaceshare.data.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun bindPreferencesRepository(
        preferencesRepoImpl: PreferencesRepoImpl
    ): PreferencesRepository
}
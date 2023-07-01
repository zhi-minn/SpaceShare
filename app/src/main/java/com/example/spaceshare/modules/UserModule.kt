package com.example.spaceshare.modules

import com.example.spaceshare.data.implementation.UserRepoImpl
import com.example.spaceshare.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindUserRepository(
        userRepoImpl: UserRepoImpl
    ): UserRepository
}
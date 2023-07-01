package com.example.spaceshare.modules

import android.app.Application
import androidx.room.Room
import com.example.spaceshare.data.dao.UserDao
import com.example.spaceshare.data.database.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideUserDatabase(application: Application): UserDatabase {
        return Room.databaseBuilder(application, UserDatabase::class.java, "user-db")
            .build()
    }
}
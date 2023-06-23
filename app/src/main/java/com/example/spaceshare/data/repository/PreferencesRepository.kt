package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Preferences

interface PreferencesRepository {

    suspend fun fetchPreferences(userId: String): Preferences?

    suspend fun updatePreferences(preferences: Preferences)
}
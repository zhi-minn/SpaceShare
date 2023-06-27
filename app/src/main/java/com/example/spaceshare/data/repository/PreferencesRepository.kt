package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Preferences
import com.google.firebase.firestore.GeoPoint

interface PreferencesRepository {

    suspend fun getPreferences(userId: String): Preferences?

    suspend fun updatePreferences(preferences: Preferences)

    suspend fun getAllPreferences(): List<Preferences>
}
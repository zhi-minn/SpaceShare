package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.PreferencesRepository
import com.example.spaceshare.models.Preferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
): PreferencesRepository {

    private val preferencesCollection = db.collection("preferences")
    companion object {
        private val TAG = this::class.simpleName
    }

    override suspend fun getPreferences(userId: String): Preferences? = withContext(Dispatchers.IO) {
        try {
            val result = preferencesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            return@withContext result.documents.firstOrNull()?.toObject(Preferences::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading preferences document: ${e.message}")
            return@withContext null
        }
    }

    override suspend fun updatePreferences(preferences: Preferences) {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Preferences>()
            preferences.userId?.let {
                preferencesCollection.document(preferences.userId)
                    .set(preferences)
                    .addOnSuccessListener {
                        Log.d(TAG, "Updated preferences")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to update preferences: ${e.message}")
                        deferred.completeExceptionally(e)
                    }
            }

            deferred.await()
        }
    }

    override suspend fun getAllPreferences(): List<Preferences> = withContext(Dispatchers.IO) {
        // TODO: Implement pagination
        try {
            val result = preferencesCollection
                .limit(500)
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    document.toObject(Preferences::class.java)
                } catch (e: Exception) {
                    Log.e(TAG, "Error casting document to Preferences object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading preferences document: ${e.message}")
            return@withContext emptyList()
        }
    }
}
package com.example.spaceshare.data.repository

import com.google.firebase.database.DatabaseReference

interface FirebaseDatabaseRepository {
    suspend fun getMessagesRef() : DatabaseReference
}
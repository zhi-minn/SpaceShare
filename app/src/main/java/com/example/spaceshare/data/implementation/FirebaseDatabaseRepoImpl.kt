package com.example.spaceshare.data.implementation

import com.example.spaceshare.data.repository.FirebaseDatabaseRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirebaseDatabaseRepoImpl  : FirebaseDatabaseRepository {
    private val db = Firebase.database
    private val messagesRef = db.reference.child("messages")

    override suspend fun getMessagesRef(): DatabaseReference {
        return messagesRef
    }
}
package com.example.spaceshare.data.implementation

import com.example.spaceshare.data.repository.MessageRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MessageRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
): MessageRepository {
    companion object {
        private val TAG = this::class.simpleName
    }
    private val _searchDB = db.collection("listings")
    override suspend fun listMessages(
        senderID: String,
        receiverID: String
    ): Map<Timestamp, String> {
        TODO("Not yet implemented")
    }
    override suspend fun searchMessages(
        senderID: String,
        receiverID: String,
        content: String
    ): Map<Timestamp, String> {
        TODO("Not yet implemented")
    }
}

package com.example.spaceshare.data.repository

import com.google.firebase.Timestamp

interface MessageRepository {
    // For users to check and search chat history
    suspend fun searchMessages(senderID: String, receiverID: String, content: String): Map<Timestamp, String>

    // For app to list all the chatting history
    suspend fun listMessages(senderID: String, receiverID: String): Map<Timestamp, String>
}
package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesRepoImpl @Inject constructor(
    db: FirebaseFirestore
) : MessagesRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val chatsCollection = db.collection("chats")

    private val realTimeDB = Firebase.database
    private val baseMessagesRef = realTimeDB.reference.child("messages")

    override fun getBaseMessagesRef() : DatabaseReference {
        return baseMessagesRef
    }

    override suspend fun createChat(memberIds : List<String>) : String? {
        // Return null if the channel already exists
        if (getChatsByMemberIds(memberIds).isNotEmpty()) {
            return null
        }

        return withContext(Dispatchers.IO) {
            val chat = Chat(members = memberIds)
            val deferred = CompletableDeferred<String>()
            chatsCollection.document(chat.id)
                .set(chat)
                .addOnSuccessListener {
                    Log.d(TAG, "Set chat with id ${chat.id}")
                    deferred.complete(chat.id)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error setting document", e)
                    deferred.completeExceptionally(e)
                }
            deferred.await()
        }
    }

    override suspend fun getChatsByMemberIds(memberIds: List<String>) : List<Chat> = withContext(Dispatchers.IO) {
        try {
            val result = chatsCollection.whereEqualTo("members", memberIds)
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    val chat = document.toObject(Chat::class.java)
                    chat?.id = document.id
                    chat
                } catch (e: Exception) {
                    Log.e(TAG, "Error casting document to Chat object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading chats query: ${e.message}")
            return@withContext emptyList()
        }
    }

    override suspend fun getChatsByUserId(userId: String): List<Chat> = withContext(Dispatchers.IO) {
        try {
            val result = chatsCollection.whereArrayContains("members", userId)
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    val chat = document.toObject(Chat::class.java)
                    chat?.id = document.id
                    chat
                } catch (e: Exception) {
                    Log.e(TAG, "Error casting document to Chat object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading chats query: ${e.message}")
            return@withContext emptyList()
        }
    }
}
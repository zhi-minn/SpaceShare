package com.example.spaceshare.data.implementation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Message
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createChat(title : String, memberIds : List<String>) : Chat {
        // Return the chat if the chat already exists
        val existingChats = getChatsByMemberIds(memberIds).filter { chat ->
            chat.title == title
        }
        if (existingChats.isNotEmpty()) {
            return existingChats.first()
        }

        // Otherwise make a new chat
        return withContext(Dispatchers.IO) {
            val chat = Chat(title = title, members = memberIds)
            val deferred = CompletableDeferred<Chat>()
            chatsCollection.document(chat.id)
                .set(chat)
                .addOnSuccessListener {
                    Log.d(TAG, "Set chat with id ${chat.id}")
                    deferred.complete(chat)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error setting document", e)
                    deferred.completeExceptionally(e)
                }
            deferred.await()
        }
    }

    override suspend fun setLastMessage(chatId: String, lastMessage: Message) {
        return withContext(Dispatchers.IO) {
            val chatRef = chatsCollection.document(chatId)

            chatRef
                .update("lastMessage", lastMessage)
                .addOnSuccessListener { Log.d(TAG, "Chat $chatId successfully updated lastMessage") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating chat document $chatId", e) }
        }
    }

    override suspend fun getChatById(chatId: String): Chat? {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Chat?>()
            chatsCollection.document(chatId)
                .get()
                .addOnSuccessListener {
                    try {
                        val chat = it.toObject(Chat::class.java)
                        deferred.complete(chat)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error casting listing with ID $chatId to Chat object: ${e.message}", e)
                        deferred.complete(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting document", e)
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
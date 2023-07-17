package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.implementation.FirebaseDatabaseRepoImpl
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.Message
import com.example.spaceshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val messagesRepo: MessagesRepository
) : ViewModel() {

    private val firebaseDatabaseRepo = FirebaseDatabaseRepoImpl()

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var chatDBRef: DatabaseReference
    private lateinit var currentUser: User
    private lateinit var currentUserPhotoURL: String

    init {
        viewModelScope.launch {
            chatDBRef = firebaseDatabaseRepo.getMessagesRef().child("global")

            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
            currentUser = userRepo.getUserById(currentUserId)!!

            currentUser.photoPath?.let {
                Firebase.storage.reference.child("profiles").child(it).downloadUrl
                    .addOnSuccessListener { url ->
                        currentUserPhotoURL = url.toString()
                    }
            }
        }
    }

    fun setChatDBRef(chatId : String) {
        chatDBRef = messagesRepo.getBaseMessagesRef().child(chatId)
    }

    fun getChatDBRef(): DatabaseReference {
        return chatDBRef
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(text: String) {
        val message = constructMessage(text, null)
        chatDBRef.push().setValue(message)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendImageMessage(uri: Uri, activityContext : FragmentActivity) {
        val message = constructMessage(null, "https://i.gifer.com/ZKZg.gif") // use loading image until actual image is uploaded
        chatDBRef.push().setValue(
            message,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.w(
                        TAG, "Unable to write message to database.",
                        databaseError.toException()
                    )
                    return@CompletionListener
                }

                // Skipping FirebaseStorageRepo here as the taskSnapshot the putFile function is needed
                // Build a StorageReference and then upload the file
                val key = databaseReference.key
                val storageReference = Firebase.storage
                    .reference
                    .child("messages")
                    .child(currentUser.id)
                    .child(key!!) // message key in Firebase RT Database
                    .child(uri.lastPathSegment!!)
                putImageInStorage(storageReference, uri, key, activityContext)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?, activityContext: FragmentActivity) {
        // First upload the image to Cloud Storage
        viewModelScope.launch {
            storageReference.putFile(uri)
                .addOnSuccessListener(
                    activityContext
                ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                    // and add it to the message.
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val message = constructMessage(null, uri.toString())
                            chatDBRef.child(key!!).setValue(message)
                        }
                }
                .addOnFailureListener(activityContext) { e ->
                    Log.w(
                        TAG,
                        "Image upload task was unsuccessful.",
                        e
                    )
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun constructMessage(text: String?, imageURL: String?): Message {
        return Message(
            text,
            getCurrentUsername(),
            currentUserPhotoURL,
            imageURL
        )
    }

    fun getCurrentUsername(): String {
        if (::currentUser.isInitialized) {
            return currentUser.firstName + " " + currentUser.lastName
        }
        return FirebaseAuth.getInstance().currentUser!!.displayName.toString()
    }
}
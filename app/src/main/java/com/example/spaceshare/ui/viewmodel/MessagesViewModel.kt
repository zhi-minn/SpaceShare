package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.implementation.FirebaseDatabaseRepoImpl
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.Message
import com.example.spaceshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val firebaseDatabaseRepo = FirebaseDatabaseRepoImpl()

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var messagesRef: DatabaseReference
    private lateinit var currentUser: User
    private lateinit var currentUserPhotoURL: String

    init {
        viewModelScope.launch {
            messagesRef = firebaseDatabaseRepo.getMessagesRef().child("global")

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

    fun getMessagesDBref(): DatabaseReference {
        return messagesRef
    }

    fun sendMessage(text: String) {
        val message = constructMessage(text, null)
        messagesRef.push().setValue(message)
    }

    fun sendImageMessage(uri: Uri, activityContext : FragmentActivity) {
        val message = constructMessage(null, "https://i.gifer.com/ZKZg.gif") // use loading image until actual image is uploaded
        messagesRef.push().setValue(
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
                    .child(key!!)
                    .child(uri.lastPathSegment!!)
                putImageInStorage(storageReference, uri, key, activityContext)
            }
        )
    }

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
                            messagesRef.child(key!!).setValue(message)
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

    private fun constructMessage(text: String?, imageURL: String?): Message {
        return Message(
            text,
            getCurrentUsername(),
            currentUserPhotoURL,
            imageURL
        )
    }

    private fun getCurrentUsername(): String {
        return currentUser.firstName + " " + currentUser.lastName
    }
}
package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Message
import com.example.spaceshare.models.User
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val messagesRepo: MessagesRepository,
    private val listingsRepo: ListingRepository,
) : ViewModel() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var chat: Chat
    private lateinit var chatDBRef: DatabaseReference
    private lateinit var currentUser: User
    private var currentUserPhotoURL: String = ""

    private lateinit var associatedListing: Listing
    private lateinit var hostUser: User

    init {
        viewModelScope.launch {
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

    fun setChat(chatToSet: Chat) {
        chat = chatToSet
        chatDBRef = messagesRepo.getBaseMessagesRef().child(chat.id)
    }

    fun getChatDBRef(): DatabaseReference {
        return chatDBRef
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(text: String) {
        viewModelScope.launch {
            val message = constructMessage(text, null)
            chatDBRef.push().setValue(message)
            updateLastMessage(message)
        }
    }

    private fun updateLastMessage(message: Message) {
        viewModelScope.launch {
            messagesRepo.setLastMessage(chat.id, message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendImageMessage(uri: Uri, activityContext: FragmentActivity) {
        val message = constructMessage(
            null,
            "https://i.gifer.com/ZKZg.gif"
        ) // use loading image until actual image is uploaded
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
    private fun putImageInStorage(
        storageReference: StorageReference,
        uri: Uri,
        key: String?,
        activityContext: FragmentActivity
    ) {
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
                            updateLastMessage(message)
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
            currentUser.id,
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

    suspend fun getAssociatedListingFromRepo(listingId: String) {
        val listing = listingsRepo.getListing(listingId)
        if (listing != null) {
            associatedListing = listing
        }
    }

    fun getAssociatedListing(): Listing {
        return associatedListing
    }

    suspend fun getHostUserFromRepo(hostId: String) {
        val host = userRepo.getUserById(hostId)
        if (host != null) {
            hostUser = host
        }
    }

    fun getAssociatedListingPrice(): Double {
        return associatedListing.price
    }

    fun getAssociatedListingHostName(): String {
        return hostUser.firstName + " " + hostUser.lastName
    }

    fun getAssociatedListingGeneralLocation(): String {
        if (associatedListing.location != null) {
            return GeocoderUtil.getGeneralLocation(
                associatedListing.location!!.latitude,
                associatedListing.location!!.longitude
            )
        }
        return ""
    }
}
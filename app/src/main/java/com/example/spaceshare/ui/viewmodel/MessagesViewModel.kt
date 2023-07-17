package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val messagesRepo: MessagesRepository
) : ViewModel() {

    companion object {
        private val TAG = this::class.simpleName
    }

    var chats = MutableLiveData<List<Chat>>()

    private lateinit var currentUser: User

    init {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
            currentUser = userRepo.getUserById(currentUserId)!!
        }
        fetchChats()
    }

    fun fetchChats() {
        viewModelScope.launch {
            chats.value =
                messagesRepo.getChatsByUserId(FirebaseAuth.getInstance().currentUser!!.uid)
                    .sortedByDescending { chat ->
                        chat.lastMessage?.timestamp
                    }
        }
    }

    suspend fun createChatWithHost(listing: Listing): Chat {
        return withContext(Dispatchers.IO) {
            val memberIds = listOf(listing.hostId, currentUser.id)

            val photoRef = FirebaseStorage.getInstance()
                .reference
                .child("spaces/${listing.photos.first()}")
            val chatPhotoURL = photoRef.downloadUrl.await().toString()

            val chat = messagesRepo.createChat(
                listing.title,
                chatPhotoURL,
                listing.hostId!!,
                memberIds as List<String>
            )
            return@withContext chat
        }
    }
}
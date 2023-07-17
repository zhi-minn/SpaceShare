package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.implementation.FirebaseDatabaseRepoImpl
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Message
import com.example.spaceshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
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
            chats.value = messagesRepo.getChatsByUserId(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }
}
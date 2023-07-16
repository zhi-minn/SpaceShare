package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.implementation.FirebaseDatabaseRepoImpl
import com.example.spaceshare.models.Message
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
) : ViewModel() {

    private lateinit var messagesRef: DatabaseReference
    private val firebaseDatabaseRepo = FirebaseDatabaseRepoImpl()

    init {
        viewModelScope.launch {
            messagesRef = firebaseDatabaseRepo.getMessagesRef()
        }
    }

    fun getMessagesDBref(): DatabaseReference {
        return messagesRef
    }

    fun sendMessage(message: Message) {
        messagesRef.push().setValue(message)
    }
}
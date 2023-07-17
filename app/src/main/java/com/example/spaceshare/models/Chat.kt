package com.example.spaceshare.models

import com.google.firebase.Timestamp
import java.util.UUID

data class Chat (
    var id: String = UUID.randomUUID().toString(),
    var title : String = "",
    var lastMessage : String = "",
    var lastMessageTimestamp: Timestamp? = null,
    val members : List<String>
)
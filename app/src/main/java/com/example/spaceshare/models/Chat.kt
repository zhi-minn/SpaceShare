package com.example.spaceshare.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class Chat @RequiresApi(Build.VERSION_CODES.O) constructor(
    var id: String = UUID.randomUUID().toString(),
    val photoURL: String? = null,
    val hostId: String? = null,
    val associatedListingId: String? = null,
    val title: String = "",
    val lastMessage: Message? = null,
    val members: List<String> = listOf(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
) {
}
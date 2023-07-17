package com.example.spaceshare.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.util.UUID

data class Chat @RequiresApi(Build.VERSION_CODES.O) constructor(
    var id: String = UUID.randomUUID().toString(),
    var title : String = "",
    var lastMessage : Message? = null,
    val members : List<String> = listOf()
) {
}
package com.example.spaceshare.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

data class Message @RequiresApi(Build.VERSION_CODES.O) constructor(
    val text: String? = null,
    val senderName: String? = null,
    val senderId: String = "",
    val profilePhotoUrl: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = Instant.now().toEpochMilli()
)

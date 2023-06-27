package com.example.spaceshare.models

import com.google.firebase.Timestamp

data class Message (
   val _senderID: String,
   val _receiverID: String,
   // #TODO Enable users to send pictures or files
   val _content: MutableMap<Timestamp, String> = mutableMapOf()
)
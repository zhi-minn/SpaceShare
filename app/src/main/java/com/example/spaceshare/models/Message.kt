package com.example.spaceshare.models

data class Message (
   val senderID: String,
   val receiverID: String,
   // #TODO Enable users to send pictures or files
   val content: String = ""
)

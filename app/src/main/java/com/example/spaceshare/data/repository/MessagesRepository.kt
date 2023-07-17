package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Chat

interface MessagesRepository {

    // Creates a chat with the members set to the memberIds
    suspend fun createChat(memberIds : List<String>) : String?

    // Gets all the chats where the members are exactly the memberIds provided
    suspend fun getChatsByMemberIds(memberIds: List<String>) : List<Chat>

    // Gets all the chats a user is a member of
    suspend fun getChatsByUserId(userId : String) : List<Chat>
}
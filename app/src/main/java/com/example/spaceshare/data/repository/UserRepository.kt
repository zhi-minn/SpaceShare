package com.example.spaceshare.data.repository

import com.example.spaceshare.models.User


interface UserRepository {
    suspend fun createUser(user: User): String

    suspend fun getUserById(userId: String): User?
}
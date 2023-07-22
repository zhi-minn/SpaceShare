package com.example.spaceshare.data.repository

import com.example.spaceshare.models.User


interface UserRepository {
    suspend fun setUser(user: User): String

    suspend fun getUserById(userId: String): User?

    suspend fun getAllUsers(): List<User>

    suspend fun updateUserVerifiedStatus(userId: String, status: Int)

    suspend fun getAdminUsers(): List<String>
}
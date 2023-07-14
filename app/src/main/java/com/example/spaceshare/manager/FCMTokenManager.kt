package com.example.spaceshare.manager

import android.content.Context
import android.content.SharedPreferences

object FCMTokenManager {
    private const val PREFS_NAME = "FCM_TOKEN_PREFS"
    private const val FCM_TOKEN_KEY = "FCM_TOKEN"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(FCM_TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(FCM_TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(FCM_TOKEN_KEY).apply()
    }
}
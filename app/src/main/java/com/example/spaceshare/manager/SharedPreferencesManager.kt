package com.example.spaceshare.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SharedPreferencesManager {
    private const val PREF_NAME = "SpaceSharePreferences"
    private lateinit var HOST_MODE_KEY: String

    private lateinit var sharedPreferences: SharedPreferences
    private val isHostModeLiveData = MutableLiveData<Boolean>()

    val isHostMode: LiveData<Boolean>
        get() = isHostModeLiveData

    fun init(context: Context, uid: String?) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        HOST_MODE_KEY = "${uid}_is_host_mode"
        isHostModeLiveData.value = isHostMode()
    }

    fun switchMode() {
        val curMode = sharedPreferences.getBoolean(HOST_MODE_KEY, false)
        sharedPreferences.edit().putBoolean(HOST_MODE_KEY, !curMode).apply()
        isHostModeLiveData.value = isHostMode()
    }

    fun isHostMode(): Boolean {
        if (!sharedPreferences.contains(HOST_MODE_KEY)) {
            sharedPreferences.edit().putBoolean(HOST_MODE_KEY, false).apply()
        }
        return sharedPreferences.getBoolean(HOST_MODE_KEY, false)
    }
}
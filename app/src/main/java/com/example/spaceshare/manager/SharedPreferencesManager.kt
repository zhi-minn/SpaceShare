package com.example.spaceshare.manager

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "SpaceSharePreferences"
    const val HOST_MODE_KEY = "is_host_mode"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun switchMode() {
        val curMode = sharedPreferences.getBoolean(HOST_MODE_KEY, false)
        sharedPreferences.edit().putBoolean(HOST_MODE_KEY, !curMode).apply()
    }

    fun isHostMode(): Boolean {
        if (!sharedPreferences.contains(HOST_MODE_KEY)) {
            sharedPreferences.edit().putBoolean(HOST_MODE_KEY, false)
        }
        return sharedPreferences.getBoolean(HOST_MODE_KEY, false)
    }

    fun registerListener(updateUI: (isHostMode: Boolean) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == HOST_MODE_KEY) {
                val isHostMode = sharedPreferences.getBoolean(key, false)
                updateUI(isHostMode)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        return listener
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
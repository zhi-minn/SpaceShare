package com.example.spaceshare

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideOption
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpaceShare : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this)
    }
}
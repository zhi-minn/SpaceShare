package com.example.spaceshare

import android.app.Application
import android.location.Geocoder
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideOption
import com.example.spaceshare.manager.FCMTokenManager
import com.example.spaceshare.utils.GeocoderUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpaceShare : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        GeocoderUtil.initialize(this)
        FCMTokenManager.initialize(this)
    }
}
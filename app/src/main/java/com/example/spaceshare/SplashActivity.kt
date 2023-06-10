package com.example.spaceshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // TODO: Use Splash Screen API
        val isAuthenticated = checkAuthenticationStatus()

        // Launch the appropriate activity
        val targetActivityClass = if (isAuthenticated) {
            MainActivity::class.java
        } else {
            AuthActivity::class.java
        }

        val intent = Intent(this, targetActivityClass)
        startActivity(intent)
        finish()
    }

    private fun checkAuthenticationStatus(): Boolean {
        return auth.currentUser != null
    }
}
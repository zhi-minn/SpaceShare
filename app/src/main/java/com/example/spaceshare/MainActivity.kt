package com.example.spaceshare

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = this::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Set content view to search screen
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Current user: %s".format(FirebaseAuth.getInstance().currentUser?.email))
    }
}
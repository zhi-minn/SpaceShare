package com.example.spaceshare.ui.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class UserViewModel @Inject constructor() {
    private val db = Firebase.firestore

    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    private val docRef = db.collection("user").document(uid)

    fun getDocRef(): DocumentReference {
        return docRef
    }
}
package com.example.spaceshare.data.implementation

import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListingRepoImpl(
    private val db: FirebaseFirestore = Firebase.firestore
): ListingRepository {

    val collectionRef = db.collection("listings")
    override fun createListing(listing: Listing) {
        TODO("Not yet implemented")
    }


}
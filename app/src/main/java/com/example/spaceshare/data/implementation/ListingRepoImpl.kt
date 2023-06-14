package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListingRepoImpl(
    private val db: FirebaseFirestore = Firebase.firestore
): ListingRepository {

    private val collection = db.collection("listings")
    override fun createListing(listing: Listing): String {
        var newListingID = ""
        collection.add(listing)
            .addOnSuccessListener { documentReference ->
                Log.d("listings", "Added listing with id ${documentReference.id}")
                newListingID = documentReference.id
            }
            .addOnFailureListener { e ->
                Log.w("listings", "Error adding document", e)
            }

        return newListingID
    }


}
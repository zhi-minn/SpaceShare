package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.models.Listing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateListingFragment : Fragment() {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        val publishButton = view.findViewById<Button>(R.id.publishButton)
        publishButton.setOnClickListener {
            val title = view.findViewById<EditText>(R.id.titleTextInput).text.toString()
            val price = view.findViewById<EditText>(R.id.priceTextInput).text.toString().toDouble()
            val description = view.findViewById<EditText>(R.id.descriptionTextInput).text.toString()

            publishListing(title, price, description)
        }
    }

    private fun publishListing(title: String, price: Double, description: String) {
        val firebaseUser = auth.currentUser

        val hostID = firebaseUser?.uid

        val listing = Listing(title = title, price = price, description = description, hostId = hostID)

        db.collection("listings").add(listing)
    }
}
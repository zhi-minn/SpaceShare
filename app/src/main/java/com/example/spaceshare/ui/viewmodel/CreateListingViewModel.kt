package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.PreferencesRepository
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.Listing
import com.example.spaceshare.utils.MailUtil
import com.example.spaceshare.utils.MathUtil
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateListingViewModel @Inject constructor(
    private val listingRepo: ListingRepository,
    private val preferencesRepo: PreferencesRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel(), LocationInterface {

    private val _imageUris: MutableLiveData<List<Uri>> = MutableLiveData()
    val imageUris: LiveData<List<Uri>> = _imageUris

    private val _location: MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = _location

    private val _listingPublished: MutableLiveData<Boolean> = MutableLiveData()
    val listingPublished: LiveData<Boolean> = _listingPublished

    fun addImageUri(imageUri: Uri) {
        val curImageUris = _imageUris.value ?: emptyList()
        val newImageUris = curImageUris.toMutableList()
        newImageUris.add(imageUri)
        _imageUris.value = newImageUris
    }

    override fun setLocation(location: LatLng) {
        _location.value = location
    }

    fun publishListing(listing: Listing) {
        viewModelScope.launch {
            val imageUris = imageUris.value

            if (!imageUris.isNullOrEmpty()) {
                val uploadTasks = imageUris.map { imageUri ->
                    viewModelScope.async {
                        try {
                            firebaseStorageRepo.uploadFile("spaces", imageUri)
                        } catch (e: Exception) {
                            // TODO: Handle file upload exception here
                            null
                        }
                    }
                }

                val imageNames = uploadTasks.awaitAll().filterNotNull()

                listing.photos.addAll(imageNames)
            }

            val locationValue = _location.value
            if (locationValue != null) {
                val geoPoint = GeoPoint(locationValue.latitude, locationValue.longitude)
                listing.location = geoPoint
            }

            try {
                listingRepo.createListing(listing)
                _listingPublished.value = true

                // Inform users whose preferences matches listing
                if (listing.location != null) {
                    preferencesRepo.getAllPreferences().forEach { preferences ->
                        Log.i("TAG", "Analyzing preference ${preferences}")
                        if (preferences.location != null && preferences.email != null) {
                            val distance = MathUtil.calculateDistanceInKilometers(listing.location!!, preferences.location!!)
                            if (distance <= preferences.radius) {
                                val body = "There is a new listing matching your preferences: ${listing.location}"
                                MailUtil.sendEmail(preferences.email, "New Listing Alert", body)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // TODO: Inform user error creating listing in UI
                Log.e("CreateListing", "Error creating listing or sending email preferences: ${e.message}")
                _listingPublished.value = false
            }
        }
    }
}
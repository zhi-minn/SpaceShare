package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateListingViewModel @Inject constructor(
    private val listingRepo: ListingRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel() {

    private val _imageUris: MutableLiveData<List<Uri>> = MutableLiveData()
    val imageUris: LiveData<List<Uri>> = _imageUris

    private val _location: MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = _location

    fun addImageUri(imageUri: Uri) {
        val curImageUris = _imageUris.value ?: emptyList()
        val newImageUris = curImageUris.toMutableList()
        newImageUris.add(imageUri)
        _imageUris.value = newImageUris
    }

    fun setLocation(location: LatLng) {
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
            listingRepo.createListing(listing)
        }
    }
}
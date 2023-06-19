package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateListingViewModel @Inject constructor(
    private val listingRepo: ListingRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel() {

    private val _imageUris: MutableLiveData<List<Uri>> = MutableLiveData()
    val imageUris: LiveData<List<Uri>> = _imageUris

    fun addImageUri(imageUri: Uri) {
        val curImageUris = _imageUris.value ?: emptyList()
        val newImageUris = curImageUris.toMutableList()
        newImageUris.add(imageUri)
        _imageUris.value = newImageUris
    }

    fun publishListing(listing: Listing) {
        viewModelScope.launch {
            listingRepo.createListing(listing)

            if (imageUris.value != null) {
                imageUris.value!!.forEach { imageUri ->
                    firebaseStorageRepo.uploadFile("spaces", imageUri)
                }
            }
        }
    }
}
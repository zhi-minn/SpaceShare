package com.example.spaceshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListingViewModel @Inject constructor(
    private val repo: ListingRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel() {

    private val _listingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val listingsLiveData: LiveData<List<Listing>> = _listingsLiveData

    fun fetchListings(user: User) {
        viewModelScope.launch {
            val listings = repo.fetchListings(user)
            _listingsLiveData.value = listings
        }
    }

     fun removeItem(listing: Listing, position: Int) {
        val updatedList = _listingsLiveData.value?.toMutableList()
        updatedList?.removeAt(position)
        _listingsLiveData.value = updatedList ?: emptyList()
        viewModelScope.launch {
            // Delete listing
            repo.deleteListing(listing.id!!)

            // Delete corresponding images
            for (filePath in listing.photos) {
                viewModelScope.async {
                    try {
                        firebaseStorageRepo.deleteFile("spaces", filePath)
                    } catch (e: Exception) {
                        Log.e("ListingViewModel", "Error deleting image: ${e.message}", e)
                    }
                }
            }
        }
    }
}
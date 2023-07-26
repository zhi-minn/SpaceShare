package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.ShortlistRepository
import com.example.spaceshare.models.Listing
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShortlistViewModel @Inject constructor(
    private val shortlistRepo: ShortlistRepository
) : ViewModel() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val mutableShortlistedListings = MutableLiveData<List<Listing>>()
    val shortlistedListings: LiveData<List<Listing>> get() = mutableShortlistedListings

    init {
        fetchShortlistedListings()
    }

    private fun fetchShortlistedListings() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val results = shortlistRepo.getShortlistedListings(currentUser.uid)
                val forceFiltered = applyForcedClientSearchFilters(results)
                mutableShortlistedListings.value = forceFiltered
            }
        }
    }

    private fun applyForcedClientSearchFilters(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            filterForActiveListings(listing) && filterForNonOwnListings(listing)
        }
    }

    private fun filterForActiveListings(listing: Listing): Boolean {
        return listing.isActive
    }

    private fun filterForNonOwnListings(listing: Listing): Boolean {
        return listing.hostId != FirebaseAuth.getInstance().currentUser?.uid
    }
}
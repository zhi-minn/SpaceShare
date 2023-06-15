package com.example.spaceshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListingViewModel @Inject constructor(
    private val repo: ListingRepository
): ViewModel() {

    private val _listingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val listingsLiveData: LiveData<List<Listing>> = _listingsLiveData

    fun fetchListings(user: User) {
        viewModelScope.launch {
            val listings = repo.fetchListings(user)
            _listingsLiveData.value = listings
        }
    }
}
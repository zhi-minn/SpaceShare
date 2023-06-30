package com.example.spaceshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class ListingViewModel @Inject constructor(
    private val repo: ListingRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel() {

    private val _listingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val listingsLiveData: LiveData<List<Listing>> = _listingsLiveData

    private val _filteredListingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val filteredListingsLiveData: LiveData<List<Listing>> = _filteredListingsLiveData

    private var curQuery: String = ""
    private var curCriteria: FilterCriteria = FilterCriteria()

    fun fetchListings(user: User) {
        viewModelScope.launch {
            val listings = repo.getUserListings(user)
            _listingsLiveData.value = listings
            filterListings(curQuery, curCriteria)
        }
    }

    fun getCriteria(): FilterCriteria {
        return curCriteria
    }

    fun setCriteria(criteria: FilterCriteria) {
        curCriteria = criteria
        filterListings(curQuery, curCriteria)
    }

    fun filterListings(query: String, criteria: FilterCriteria) {
        curQuery = query
        curCriteria = criteria

        var criteriaListings = _listingsLiveData.value.orEmpty().filter { listing ->
            listing.price >= criteria.minPrice
                    && listing.price <= criteria.maxPrice
                    && listing.spaceAvailable >= criteria.minSpace
                    && listing.spaceAvailable <= criteria.maxSpace
        }
        // If 'Active Listings' filter not checked, remove active listings
        if (!criteria.isActive) {
            criteriaListings = criteriaListings.filterNot { listing -> listing.isActive }
        }
        // If 'Inactive Listings' filter not checked, remove inactive listings
        if (!criteria.isInactive) {
            criteriaListings = criteriaListings.filterNot { listing -> !listing.isActive }
        }

        if (query.isEmpty()) {
            _filteredListingsLiveData.value = criteriaListings
            return
        }

        // Filter by price or space available
        val value = query.toDoubleOrNull()
        if (value != null) {
            _filteredListingsLiveData.value = criteriaListings.filter { listing ->
                listing.price == value || listing.spaceAvailable == value
            }
            return
        }

        // Filter by title
        _filteredListingsLiveData.value = criteriaListings.filter { listing ->
            listing.title.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
        }
    }

    fun updateListing(listing: Listing) {
        val newList = _listingsLiveData.value.orEmpty().toMutableList()

        viewModelScope.launch {
            if (repo.updateListing(listing)) {
                for (index in 0 until newList.size) {
                    if (newList[index].id == listing.id) {
                        newList[index] = listing
                    }
                    _listingsLiveData.value = newList
                    filterListings(curQuery, curCriteria)
                    break
                }
            }
        }
    }

    fun addItem(listing: Listing) {
        val currentList = _listingsLiveData.value.orEmpty().toMutableList()
        currentList.add(0, listing)
        _listingsLiveData.value = currentList
    }

     fun removeItem(listing: Listing) {
         val updatedList = _listingsLiveData.value?.toMutableList()
         updatedList?.remove(listing)
         _listingsLiveData.value = updatedList ?: emptyList()
         filterListings(curQuery, curCriteria)
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
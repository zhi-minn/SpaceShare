package com.example.spaceshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Delete
import com.example.spaceshare.consts.ListingConsts
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.models.Listing
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

import com.example.spaceshare.consts.ListingConsts.DEFAULT_MAX_PRICE
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.data.repository.ReservationRepository
import java.math.BigDecimal
import java.math.RoundingMode

class ListingViewModel @Inject constructor(
    private val listingRepo: ListingRepository,
    private val reservationRepo: ReservationRepository,
    private val messagesRepo: MessagesRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
): ViewModel() {

    private val _listingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val listingsLiveData: LiveData<List<Listing>> = _listingsLiveData

    private val _filteredListingsLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val filteredListingsLiveData: LiveData<List<Listing>> = _filteredListingsLiveData

    private val _listingRevenue: MutableLiveData<Double> = MutableLiveData(0.00)
    val listingRevenue: LiveData<Double> = _listingRevenue

    data class DeleteResult(
        val isSuccess: Boolean,
        val message: String
    )
    private val _deleteResult: MutableLiveData<DeleteResult> = MutableLiveData()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    private var curQuery: String = ""
    private var curCriteria: FilterCriteria = FilterCriteria()

    private val mutableHasFilterBeenApplied = MutableLiveData<Boolean>(false)
    val hasFilterBeenApplied: LiveData<Boolean> get() = mutableHasFilterBeenApplied

    fun getUserListings(userId: String) {
        viewModelScope.launch {
            val listings = listingRepo.getUserListings(userId)
            Log.i("Listings", "Fetched $listings")
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
    fun getListingsMaxPrice(): Double {
        val listingsSnapshot = listingsLiveData.value
        if (!listingsSnapshot.isNullOrEmpty()) {
            var maxPrice = listingsSnapshot.maxOf { listing ->
                listing.price
            }
            if (maxPrice == 0.0) {
                maxPrice = ListingConsts.DEFAULT_MAX_PRICE.toDouble()
            }

            return maxPrice
        }
        return ListingConsts.DEFAULT_MAX_PRICE.toDouble()
    }

    fun getListingsMaxSpace(): Double {
        val listingsSnapshot = listingsLiveData.value
        if (!listingsSnapshot.isNullOrEmpty()) {
            var maxSpace = listingsSnapshot.maxOf { listing ->
                listing.spaceAvailable
            }
            if (maxSpace == 0.0) {
                maxSpace = ListingConsts.SPACE_UPPER_LIMIT
            }
            return maxSpace
        }
        return ListingConsts.SPACE_UPPER_LIMIT
    }

    fun setHasFilterBeenApplied(beenApplied: Boolean) {
        mutableHasFilterBeenApplied.value = beenApplied
    }

    fun filterListings(query: String, criteria: FilterCriteria) {
        curQuery = query
        curCriteria = criteria

        val prices = listingsLiveData.value?.map { it.price }
        val maxPrice = prices?.maxOrNull() ?: DEFAULT_MAX_PRICE
        var criteriaMaxPrice = criteria.maxPrice ?: maxPrice.toFloat()
        val roundedCriteriaMaxPrice = BigDecimal(criteriaMaxPrice.toDouble())
            .setScale(2, RoundingMode.HALF_EVEN).toDouble()

        var criteriaListings = _listingsLiveData.value.orEmpty().filter { listing ->
            listing.price >= criteria.minPrice
                    && listing.price <= roundedCriteriaMaxPrice
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
            if (listingRepo.updateListing(listing)) {
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
        // Remove item first in case we are updating a listing
        val currentPos = currentList.indexOf(listing)
        if (currentPos != -1) {
            currentList.removeAt(currentPos)
        }
        currentList.add(0, listing)
        _listingsLiveData.value = currentList
        filterListings(curQuery, curCriteria)
    }

     fun removeItem(listing: Listing) {
         // Check if any associated reservations first
         viewModelScope.launch {
             val upcomingReservations =
                 reservationRepo.fetchUpcomingReservationByListingId(listing.id)
             if (upcomingReservations.isNotEmpty()) {
                 _deleteResult.value = DeleteResult(false, "Unable to delete listing with upcoming reservations. Consider deactivating the listing.")
             } else {
                 val updatedList = _listingsLiveData.value?.toMutableList()
                 updatedList?.remove(listing)
                 _listingsLiveData.value = updatedList ?: emptyList()
                 filterListings(curQuery, curCriteria)
                 viewModelScope.launch {
                     // Delete listing
                     listingRepo.deleteListing(listing.id)

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

                     // Delete chats associated
                     messagesRepo.deleteChatsByAssociatedListingId(listing.id)

                     _deleteResult.value = DeleteResult(true, "Successfully deleted listing")
                 }
             }
         }
     }

    fun fetchListingRevenue(listingId: String) {
        viewModelScope.launch {
            val completedReservations = reservationRepo.fetchCompletedReservationsByListing(listingId)
            _listingRevenue.value = completedReservations.sumOf { it.totalCost }
        }
    }
}
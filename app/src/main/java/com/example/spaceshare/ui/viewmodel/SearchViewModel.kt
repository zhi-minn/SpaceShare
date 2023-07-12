package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.consts.ListingConsts
import com.example.spaceshare.consts.ListingConsts.SPACE_BOOKING_LOWER_LIMIT
import com.example.spaceshare.consts.ListingConsts.SPACE_UPPER_LIMIT
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.sql.Date
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val listingRepo: ListingRepository
) : ViewModel(), LocationInterface {

    var spaceRequired = MutableLiveData<Double>(0.0)

    var listings = MutableLiveData<List<Listing>>()

    var location = MutableLiveData<LatLng>()
    var searchRadius = MutableLiveData<Float>()

    var startTime = MutableLiveData<Long>()
    var endTime = MutableLiveData<Long>()

    var filterCriteria = FilterCriteria()

    init {
        location.value = LatLng(0.0, 0.0)
        startTime.value = 0
        endTime.value = 0
        searchRadius.value = 1f
        fetchInitialListings()
    }

    private fun fetchInitialListings() {
        viewModelScope.launch {
            val results = listingRepo.getAllListings()
            listings.value = applyClientSearchFilters(results)
        }
    }

    fun incrementSpaceRequired() {
        if (spaceRequired.value?.plus(0.5)!! < SPACE_UPPER_LIMIT)
            spaceRequired.value = spaceRequired.value?.plus(0.5)
    }

    fun decrementSpaceRequired() {
        if (spaceRequired.value?.minus(0.5)!! >= SPACE_BOOKING_LOWER_LIMIT)
            spaceRequired.value = spaceRequired.value?.minus(0.5)
    }

    fun submitSearch() {
        viewModelScope.launch {
            val searchLoc: LatLng = location.value!!
            val searchGeopoint = GeoPoint(searchLoc.latitude, searchLoc.longitude)

            val startDate = Date(startTime.value!!)
            val endDate = Date(endTime.value!!)
            val startTimestamp = Timestamp(startDate)
            val endTimestamp = Timestamp(endDate)

            // If location is not set, make query radius slightly larger than the longest possible
            //  distance on Earth (basically don't filter by radius/get all results)
            var queryRadius = searchRadius.value!!
            if (location.value == LatLng(0.0, 0.0))
                queryRadius = 20500f

            val criteria =
                SearchCriteria(
                    spaceRequired.value!!, searchGeopoint,
                    queryRadius, startTimestamp, endTimestamp
                )
            val searchResults = listingRepo.searchListings(criteria)
            listings.value = applyClientSearchFilters(searchResults)
        }
    }

    fun filterByFilterCriteria() {
        val filteredByCriteriaListings = listings.value.orEmpty().filter { listing ->
            val prices = listings.value?.map { it.price }
            var maxPrice = prices?.maxOrNull()?.toFloat() ?: ListingConsts.DEFAULT_MAX_PRICE
            if (maxPrice == 0.0f) maxPrice = ListingConsts.DEFAULT_MAX_PRICE
            val filterCriteriaMaxPrice = filterCriteria.maxPrice ?: maxPrice

            listing.price >= filterCriteria.minPrice
                    && listing.price <= filterCriteriaMaxPrice
                    && listing.spaceAvailable >= filterCriteria.minSpace
                    && listing.spaceAvailable <= filterCriteria.maxSpace
        }

        listings.value = filteredByCriteriaListings
    }

    fun getSearchViewModel(): SearchViewModel {
        return this
    }

    private fun applyClientSearchFilters(listings: List<Listing>): List<Listing> {
        val nonOwnListings = filterForNonOwnListings(listings)
        val activeListings = filterForActiveListings(nonOwnListings)
        return activeListings
    }

    private fun filterForActiveListings(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            listing.isActive
        }
    }

    private fun filterForNonOwnListings(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            listing.hostId != FirebaseAuth.getInstance().currentUser?.uid
        }
    }

    override fun setLocation(latLng: LatLng) {
        location.value = latLng
    }
}
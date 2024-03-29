package com.example.spaceshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.consts.ListingConsts
import com.example.spaceshare.consts.ListingConsts.SPACE_BOOKING_LOWER_LIMIT
import com.example.spaceshare.consts.ListingConsts.SPACE_UPPER_LIMIT
import com.example.spaceshare.data.implementation.ListingRepoImpl
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.enums.FilterSortByOption
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.FilterCriteria
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.utils.MathUtil
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

    companion object {
        private val TAG = this::class.simpleName
    }

    private val mutableSpaceRequired = MutableLiveData<Double>(0.0)
    val spaceRequired: LiveData<Double> get() = mutableSpaceRequired

    private val mutableSearchResults = MutableLiveData<List<Listing>>()
    val searchResults: LiveData<List<Listing>> get() = mutableSearchResults

    private val mutableFilteredListings = MutableLiveData<List<Listing>>()
    val filteredListings: LiveData<List<Listing>> get() = mutableFilteredListings

    private val mutableLocation = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> get() = mutableLocation

    private val mutableSearchRadius = MutableLiveData<Float>()
    val searchRadius: LiveData<Float> get() = mutableSearchRadius

    private val mutableStartTime = MutableLiveData<Long>()
    private val mutableEndTime = MutableLiveData<Long>()
    val startTime: LiveData<Long> get() = mutableStartTime
    val endTime: LiveData<Long> get() = mutableEndTime

    private val mutableFilterCriteria = MutableLiveData<FilterCriteria>()
    val filterCriteria: LiveData<FilterCriteria> get() = mutableFilterCriteria

    private val mutableHasFilterBeenApplied = MutableLiveData<Boolean>(false)
    val hasFilterBeenApplied: LiveData<Boolean> get() = mutableHasFilterBeenApplied

    private val mutableSortByOption = MutableLiveData<FilterSortByOption>(FilterSortByOption.CLOSEST)
    val sortByOption: LiveData<FilterSortByOption> get() = mutableSortByOption

    init {
        mutableLocation.value = LatLng(0.0, 0.0)
        mutableStartTime.value = 0
        mutableEndTime.value = 0
        mutableSearchRadius.value = 1f
        mutableFilterCriteria.value = FilterCriteria()
        fetchInitialListings()
    }

    private fun fetchInitialListings() {
        viewModelScope.launch {
            val results = listingRepo.getAllListings()
            val forceFiltered = applyForcedClientSearchFilters(results)
            mutableSearchResults.value = sortByDistance(forceFiltered)
            mutableFilteredListings.value = searchResults.value
        }
    }

    fun incrementSpaceRequired() {
        if (mutableSpaceRequired.value?.plus(0.5)!! < SPACE_UPPER_LIMIT)
            mutableSpaceRequired.value = mutableSpaceRequired.value?.plus(0.5)
    }

    fun decrementSpaceRequired() {
        if (mutableSpaceRequired.value?.minus(0.5)!! >= SPACE_BOOKING_LOWER_LIMIT)
            mutableSpaceRequired.value = spaceRequired.value?.minus(0.5)
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
            val repoSearchResults = listingRepo.searchListings(criteria)
            Log.d(TAG, searchResults.toString())
            mutableSearchResults.value = applyForcedClientSearchFilters(repoSearchResults)

            // Check filter criteria for invalid values, then apply it if necessary
            var filterBeenApplied = hasFilterBeenApplied.value
            if (filterBeenApplied == null) {
                filterBeenApplied = false
            }
            if (filterBeenApplied) {
                checkFilterCriteriaMaxValues()
                filterByFilterCriteria()
            }
            else {
                mutableFilteredListings.value = sortByDistance(searchResults.value.orEmpty())
            }
        }
    }

    fun getSearchResultsMaxPrice(): Double {
        val searchResultsSnapshot = searchResults.value
        if (!searchResultsSnapshot.isNullOrEmpty()) {
            var maxPrice = searchResultsSnapshot.maxOf { listing ->
                listing.price
            }
            if (maxPrice == 0.0) {
                maxPrice = ListingConsts.DEFAULT_MAX_PRICE.toDouble()
            }

            return maxPrice
        }
        return ListingConsts.DEFAULT_MAX_PRICE.toDouble()
    }

    fun getSearchResultsMaxSpace(): Double {
        val searchResultsSnapshot = searchResults.value
        if (!searchResultsSnapshot.isNullOrEmpty()) {
            var maxSpace = searchResultsSnapshot.maxOf { listing ->
                listing.spaceAvailable
            }
            if (maxSpace == 0.0) {
                maxSpace = ListingConsts.SPACE_UPPER_LIMIT
            }
            return maxSpace
        }
        return ListingConsts.SPACE_UPPER_LIMIT
    }

    private fun checkFilterCriteriaMaxValues() {
        val criteria = filterCriteria.value
        if (criteria != null) {
            var searchResultsMaxPrice = getSearchResultsMaxPrice()
            if (criteria.maxPrice > searchResultsMaxPrice) {
                criteria.maxPrice = searchResultsMaxPrice.toFloat()
            }

            var searchResultsMaxSpace = getSearchResultsMaxSpace()
            if (criteria.maxSpace > searchResultsMaxSpace) {
                criteria.maxSpace = searchResultsMaxSpace.toFloat()
            }

            mutableFilterCriteria.value = criteria!!
        }
    }

    fun filterByFilterCriteria() {
        val criteria = filterCriteria.value
        if (criteria != null) {
            val filteredByCriteriaListings = searchResults.value.orEmpty().filter { listing ->
                listing.price >= criteria.minPrice
                        && listing.price <= criteria.maxPrice
                        && listing.spaceAvailable >= criteria.minSpace
                        && listing.spaceAvailable <= criteria.maxSpace
                        && listing.amenities.containsAll(criteria.amenities)
            }

            setHasFilterBeenApplied(true)

            mutableFilteredListings.value = sortBySelectedSortOption(filteredByCriteriaListings)
        }
    }

    private fun sortBySelectedSortOption(listings: List<Listing>): List<Listing> {
        return when (sortByOption.value) {
            FilterSortByOption.CLOSEST ->
                sortByDistance(listings)
            FilterSortByOption.NEWEST ->
                sortByNewest(listings)
            FilterSortByOption.OLDEST ->
                sortByOldest(listings)
            FilterSortByOption.CHEAPEST ->
                sortByCheapest(listings)
            FilterSortByOption.MOST_EXPENSIVE ->
                sortByMostExpensive(listings)
            FilterSortByOption.LARGEST ->
                sortByLargest(listings)
            FilterSortByOption.SMALLEST ->
                sortBySmallest(listings)

            else ->
                emptyList()
        }
    }

    private fun sortBySmallest(listings: List<Listing>): List<Listing> {
        return listings.sortedBy { listing ->
            listing.spaceAvailable
        }
    }

    private fun sortByLargest(listings: List<Listing>): List<Listing> {
        return listings.sortedByDescending { listing ->
            listing.spaceAvailable
        }
    }

    private fun sortByMostExpensive(listings: List<Listing>): List<Listing> {
        return listings.sortedByDescending { listing ->
            listing.price
        }
    }

    private fun sortByCheapest(listings: List<Listing>): List<Listing> {
        return listings.sortedBy { listing ->
            listing.price
        }
    }

    private fun sortByOldest(listings: List<Listing>): List<Listing> {
        return listings.sortedBy { listing ->
            listing.createdAt
        }
    }

    private fun sortByNewest(listings: List<Listing>): List<Listing> {
        return listings.sortedByDescending { listing ->
            listing.createdAt
        }
    }

    private fun sortByDistance(listings: List<Listing>): List<Listing> {
        val targetLocation = GeoPoint(mutableLocation.value!!.latitude, mutableLocation.value!!.longitude)
        return listings.sortedBy { listing ->
            try {
                val dist = MathUtil.calculateDistanceInKilometers(
                    targetLocation,
                    listing.location!!
                )
                dist
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error calculating and sorting by Listing distance for id ${listing.id}: ${e.message}"
                )
                null
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

    override fun setLocation(latLng: LatLng) {
        mutableLocation.value = latLng
    }

    fun setFilterCriteria(criteria: FilterCriteria) {
        mutableFilterCriteria.value = criteria
    }

    fun setStartTime(timestamp: Long) {
        mutableStartTime.value = timestamp
    }

    fun setEndTime(timestamp: Long) {
        mutableEndTime.value = timestamp
    }

    fun setSearchRadius(radius: Float) {
        mutableSearchRadius.value = radius
    }

    fun setSpaceRequired(unit: Double){
        mutableSpaceRequired.value = unit
    }

    private fun setHasFilterBeenApplied(beenApplied: Boolean) {
        mutableHasFilterBeenApplied.value = beenApplied
    }

    fun setSortByOption(option: FilterSortByOption) {
        mutableSortByOption.value = option
    }
}
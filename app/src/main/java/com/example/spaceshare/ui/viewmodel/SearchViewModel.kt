package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.consts.ListingConsts.SPACE_BOOKING_LOWER_LIMIT
import com.example.spaceshare.consts.ListingConsts.SPACE_UPPER_LIMIT
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.interfaces.ListingAdapterInterface
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.models.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
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

    var startTime = MutableLiveData<Long>()
    var endTime = MutableLiveData<Long>()

    init {
        location.value = LatLng(0.0,0.0)
        startTime.value = 0
        endTime.value = 0
        fetchInitialListings()
    }

    fun fetchInitialListings() {
        viewModelScope.launch {
            val results = listingRepo.getAllListings()
            listings.value = results
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

    fun clearAllDialogData() {
        // TODO: Find some way to reset the date range picker
        location?.value = null
        spaceRequired.value = 0.0
        startTime.value = 0
        endTime.value = 0
    }

    fun submitSearch() {
        viewModelScope.launch {
            val searchLoc: LatLng = location.value!!
            val searchGeopoint = GeoPoint(searchLoc.latitude, searchLoc.longitude)
            val startDate = Date(startTime.value!!)
            val endDate = Date(endTime.value!!)
            val startTimestamp = Timestamp(startDate)
            val endTimestamp = Timestamp(endDate)
            val criteria =
                SearchCriteria(spaceRequired.value!!, searchGeopoint, startTimestamp, endTimestamp)
            listingRepo.searchListings(criteria)
        }
    }

    override fun setLocation(latLng: LatLng) {
        location?.value = latLng
    }

}